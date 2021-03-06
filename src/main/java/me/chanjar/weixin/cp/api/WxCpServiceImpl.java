package me.chanjar.weixin.cp.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.WxMenu;
import me.chanjar.weixin.common.bean.result.WxError;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.StandardSessionManager;
import me.chanjar.weixin.common.session.WxSession;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.common.util.RandomUtils;
import me.chanjar.weixin.common.util.StringUtils;
import me.chanjar.weixin.common.util.crypto.SHA1;
import me.chanjar.weixin.common.util.fs.FileUtils;
import me.chanjar.weixin.common.util.http.MediaDownloadRequestExecutor;
import me.chanjar.weixin.common.util.http.MediaUploadRequestExecutor;
import me.chanjar.weixin.common.util.http.RequestExecutor;
import me.chanjar.weixin.common.util.http.SimpleGetRequestExecutor;
import me.chanjar.weixin.common.util.http.SimplePostRequestExecutor;
import me.chanjar.weixin.common.util.http.URIUtil;
import me.chanjar.weixin.common.util.json.GsonHelper;
import me.chanjar.weixin.cp.bean.WxCpDepart;
import me.chanjar.weixin.cp.bean.WxCpMessage;
import me.chanjar.weixin.cp.bean.WxCpTag;
import me.chanjar.weixin.cp.bean.WxCpUser;
import me.chanjar.weixin.cp.util.json.WxCpGsonBuilder;

public class WxCpServiceImpl implements WxCpService {

	private static final Logger log = Logger.getLogger(WxCpServiceImpl.class);

  /**
   * 鍏ㄥ眬鐨勬槸鍚︽鍦ㄥ埛鏂癮ccess token鐨勯攣
   */
  protected final Object globalAccessTokenRefreshLock = new Object();

  /**
   * 鍏ㄥ眬鐨勬槸鍚︽鍦ㄥ埛鏂癹sapi_ticket鐨勯攣
   */
  protected final Object globalJsapiTicketRefreshLock = new Object();

  protected WxCpConfigStorage wxCpConfigStorage;

  protected CloseableHttpClient httpClient;

  protected HttpHost httpProxy;

  private int retrySleepMillis = 1000;

  private int maxRetryTimes = 5;

  protected WxSessionManager sessionManager = new StandardSessionManager();

  /**
   * 涓存椂鏂囦欢鐩綍
   */
  protected File tmpDirFile;

  public boolean checkSignature(String msgSignature, String timestamp, String nonce, String data) {
    try {
      return SHA1.gen(wxCpConfigStorage.getToken(), timestamp, nonce, data).equals(msgSignature);
    } catch (Exception e) {
      return false;
    }
  }

  public void userAuthenticated(String userId) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/user/authsucc?userid=" + userId;
    get(url, null);
  }

  public String getAccessToken() throws WxErrorException {
    return getAccessToken(false);
  }

  public String getAccessToken(boolean forceRefresh) throws WxErrorException {
    if (forceRefresh) {
      wxCpConfigStorage.expireAccessToken();
    }
    if (wxCpConfigStorage.isAccessTokenExpired()) {
      synchronized (globalAccessTokenRefreshLock) {
        if (wxCpConfigStorage.isAccessTokenExpired()) {
          String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?"
              + "&corpid=" + wxCpConfigStorage.getCorpId()
              + "&corpsecret=" + wxCpConfigStorage.getCorpSecret();
          try {
            HttpGet httpGet = new HttpGet(url);
            if (httpProxy != null) {
              RequestConfig config = RequestConfig.custom().setProxy(httpProxy).build();
              httpGet.setConfig(config);
            }
            CloseableHttpClient httpclient = getHttpclient();
            String resultContent = null;
            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
              resultContent = new BasicResponseHandler().handleResponse(response);
            }
            WxError error = WxError.fromJson(resultContent);
            if (error.getErrorCode() != 0) {
              throw new WxErrorException(error);
            }
            WxAccessToken accessToken = WxAccessToken.fromJson(resultContent);
            wxCpConfigStorage.updateAccessToken(accessToken.getAccessToken(), accessToken.getExpiresIn());
          } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      }
    }
    return wxCpConfigStorage.getAccessToken();
  }

  public String getJsapiTicket() throws WxErrorException {
    return getJsapiTicket(false);
  }

  public String getJsapiTicket(boolean forceRefresh) throws WxErrorException {
    if (forceRefresh) {
      wxCpConfigStorage.expireJsapiTicket();
    }
    if (wxCpConfigStorage.isJsapiTicketExpired()) {
      synchronized (globalJsapiTicketRefreshLock) {
        if (wxCpConfigStorage.isJsapiTicketExpired()) {
          String url = "https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket";
          String responseContent = execute(new SimpleGetRequestExecutor(), url, null);
          JsonElement tmpJsonElement = Streams.parse(new JsonReader(new StringReader(responseContent)));
          JsonObject tmpJsonObject = tmpJsonElement.getAsJsonObject();
          String jsapiTicket = tmpJsonObject.get("ticket").getAsString();
          int expiresInSeconds = tmpJsonObject.get("expires_in").getAsInt();
          wxCpConfigStorage.updateJsapiTicket(jsapiTicket, expiresInSeconds);
        }
      }
    }
    return wxCpConfigStorage.getJsapiTicket();
  }

  public WxJsapiSignature createJsapiSignature(String url) throws WxErrorException {
    long timestamp = System.currentTimeMillis() / 1000;
    String noncestr = RandomUtils.getRandomStr();
    String jsapiTicket = getJsapiTicket(false);
    try {
      String signature = SHA1.genWithAmple(
          "jsapi_ticket=" + jsapiTicket,
          "noncestr=" + noncestr,
          "timestamp=" + timestamp,
          "url=" + url
      );
      WxJsapiSignature jsapiSignature = new WxJsapiSignature();
      jsapiSignature.setTimestamp(timestamp);
      jsapiSignature.setNoncestr(noncestr);
      jsapiSignature.setUrl(url);
      jsapiSignature.setSignature(signature);
      return jsapiSignature;
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  public void messageSend(WxCpMessage message) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send";
    log.info(post(url, message.toJson()));
  }

  @Override
  public void menuCreate(WxMenu menu) throws WxErrorException {
    menuCreate(wxCpConfigStorage.getAgentId(), menu);
  }

  @Override
  public void menuCreate(String agentId, WxMenu menu) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/menu/create?agentid=" + wxCpConfigStorage.getAgentId();
    post(url, menu.toJson());
  }

  @Override
  public void menuDelete() throws WxErrorException {
    menuDelete(wxCpConfigStorage.getAgentId());
  }

  @Override
  public void menuDelete(String agentId) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/menu/delete?agentid=" + agentId;
    get(url, null);
  }

  @Override
  public WxMenu menuGet() throws WxErrorException {
    return menuGet(wxCpConfigStorage.getAgentId());
  }

  @Override
  public WxMenu menuGet(String agentId) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/menu/get?agentid=" + agentId;
    try {
      String resultContent = get(url, null);
      return WxMenu.fromJson(resultContent);
    } catch (WxErrorException e) {
      // 46003 涓嶅瓨鍦ㄧ殑鑿滃崟鏁版嵁
      if (e.getError().getErrorCode() == 46003) {
        return null;
      }
      throw e;
    }
  }

  public WxMediaUploadResult mediaUpload(String mediaType, String fileType, InputStream inputStream)
      throws WxErrorException, IOException {
    return mediaUpload(mediaType, FileUtils.createTmpFile(inputStream, UUID.randomUUID().toString(), fileType));
  }

  public WxMediaUploadResult mediaUpload(String mediaType, File file) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?type=" + mediaType;
    return execute(new MediaUploadRequestExecutor(), url, file);
  }

  public File mediaDownload(String media_id) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/media/get";
    return execute(new MediaDownloadRequestExecutor(wxCpConfigStorage.getTmpDirFile()), url, "media_id=" + media_id);
  }


  public Integer departCreate(WxCpDepart depart) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/department/create";
    String responseContent = execute(
        new SimplePostRequestExecutor(),
        url,
        depart.toJson());
    JsonElement tmpJsonElement = Streams.parse(new JsonReader(new StringReader(responseContent)));
    return GsonHelper.getAsInteger(tmpJsonElement.getAsJsonObject().get("id"));
  }

  public void departUpdate(WxCpDepart group) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/department/update";
    post(url, group.toJson());
  }

  public void departDelete(Integer departId) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/department/delete?id=" + departId;
    get(url, null);
  }

  public List<WxCpDepart> departGet() throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/department/list";
    String responseContent = get(url, null);
    /*
     * 鎿嶈泲鐨勫井淇PI锛屽垱寤烘椂杩斿洖鐨勬槸 { group : { id : ..., name : ...} }
     * 鏌ヨ鏃惰繑鍥炵殑鏄� { groups : [ { id : ..., name : ..., count : ... }, ... ] }
     */
    JsonElement tmpJsonElement = Streams.parse(new JsonReader(new StringReader(responseContent)));
    return WxCpGsonBuilder.INSTANCE.create()
        .fromJson(
            tmpJsonElement.getAsJsonObject().get("department"),
            new TypeToken<List<WxCpDepart>>() {
            }.getType()
        );
  }

  @Override
  public void userCreate(WxCpUser user) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/user/create";
    post(url, user.toJson());
  }

  @Override
  public void userUpdate(WxCpUser user) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/user/update";
    post(url, user.toJson());
  }

  @Override
  public void userDelete(String userid) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/user/delete?userid=" + userid;
    get(url, null);
  }

  @Override
  public void userDelete(String[] userids) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/user/batchdelete";
    JsonObject jsonObject = new JsonObject();
    JsonArray jsonArray = new JsonArray();
    for (int i = 0; i < userids.length; i++) {
      jsonArray.add(new JsonPrimitive(userids[i]));
    }
    jsonObject.add("useridlist", jsonArray);
    post(url, jsonObject.toString());
  }

  @Override
  public WxCpUser userGet(String userid) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/user/get?userid=" + userid;
    String responseContent = get(url, null);
    return WxCpUser.fromJson(responseContent);
  }

  @Override
  public List<WxCpUser> userList(Integer departId, Boolean fetchChild, Integer status) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/user/list?department_id=" + departId;
    String params = "";
    if (fetchChild != null) {
      params += "&fetch_child=" + (fetchChild ? "1" : "0");
    }
    if (status != null) {
      params += "&status=" + status;
    } else {
      params += "&status=0";
    }

    String responseContent = get(url, params);
    JsonElement tmpJsonElement = Streams.parse(new JsonReader(new StringReader(responseContent)));
    return WxCpGsonBuilder.INSTANCE.create()
        .fromJson(
            tmpJsonElement.getAsJsonObject().get("userlist"),
            new TypeToken<List<WxCpUser>>() { }.getType()
        );
  }

  @Override
  public List<WxCpUser> departGetUsers(Integer departId, Boolean fetchChild, Integer status) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/user/simplelist?department_id=" + departId;
    String params = "";
    if (fetchChild != null) {
      params += "&fetch_child=" + (fetchChild ? "1" : "0");
    }
    if (status != null) {
      params += "&status=" + status;
    } else {
      params += "&status=0";
    }

    String responseContent = get(url, params);
    JsonElement tmpJsonElement = Streams.parse(new JsonReader(new StringReader(responseContent)));
    return WxCpGsonBuilder.INSTANCE.create()
        .fromJson(
            tmpJsonElement.getAsJsonObject().get("userlist"),
            new TypeToken<List<WxCpUser>>() { }.getType()
        );
  }

  @Override
  public String tagCreate(String tagName) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/tag/create";
    JsonObject o = new JsonObject();
    o.addProperty("tagname", tagName);
    String responseContent = post(url, o.toString());
    JsonElement tmpJsonElement = Streams.parse(new JsonReader(new StringReader(responseContent)));
    return tmpJsonElement.getAsJsonObject().get("tagid").getAsString();
  }

  @Override
  public void tagUpdate(String tagId, String tagName) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/tag/update";
    JsonObject o = new JsonObject();
    o.addProperty("tagid", tagId);
    o.addProperty("tagname", tagName);
    post(url, o.toString());
  }

  @Override
  public void tagDelete(String tagId) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/tag/delete?tagid=" + tagId;
    get(url, null);
  }

  @Override
  public List<WxCpTag> tagGet() throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/tag/list";
    String responseContent = get(url, null);
    JsonElement tmpJsonElement = Streams.parse(new JsonReader(new StringReader(responseContent)));
    return WxCpGsonBuilder.INSTANCE.create()
        .fromJson(
            tmpJsonElement.getAsJsonObject().get("taglist"),
            new TypeToken<List<WxCpTag>>() {
            }.getType()
        );
  }

  @Override
  public List<WxCpUser> tagGetUsers(String tagId) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/tag/get?tagid=" + tagId;
    String responseContent = get(url, null);
    JsonElement tmpJsonElement = Streams.parse(new JsonReader(new StringReader(responseContent)));
    return WxCpGsonBuilder.INSTANCE.create()
        .fromJson(
            tmpJsonElement.getAsJsonObject().get("userlist"),
            new TypeToken<List<WxCpUser>>() { }.getType()
        );
  }

  @Override
  public void tagAddUsers(String tagId, List<String> userIds, List<String> partyIds) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/tag/addtagusers";
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("tagid", tagId);
    if (userIds != null) {
      JsonArray jsonArray = new JsonArray();
      for (String userId : userIds) {
        jsonArray.add(new JsonPrimitive(userId));
      }
      jsonObject.add("userlist", jsonArray);
    }
    if (partyIds != null) {
      JsonArray jsonArray = new JsonArray();
      for (String userId : partyIds) {
        jsonArray.add(new JsonPrimitive(userId));
      }
      jsonObject.add("partylist", jsonArray);
    }
    post(url, jsonObject.toString());
  }

  @Override
  public void tagRemoveUsers(String tagId, List<String> userIds) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/tag/deltagusers";
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("tagid", tagId);
    JsonArray jsonArray = new JsonArray();
    for (String userId : userIds) {
      jsonArray.add(new JsonPrimitive(userId));
    }
    jsonObject.add("userlist", jsonArray);
    post(url, jsonObject.toString());
  }

  @Override
  public String oauth2buildAuthorizationUrl(String redirectUri, String state) {
    String url = "https://open.weixin.qq.com/connect/oauth2/authorize?" ;
    url += "appid=" + wxCpConfigStorage.getCorpId();
    url += "&redirect_uri=" + URIUtil.encodeURIComponent(redirectUri);
    url += "&response_type=code";
    url += "&scope=snsapi_base";
    if (state != null) {
      url += "&state=" + state;
    }
    url += "#wechat_redirect";
    return url;
  }

  @Override
  public String[] oauth2getUserInfo(String code) throws WxErrorException {
    return oauth2getUserInfo(wxCpConfigStorage.getAgentId(), code);
  }

  @Override
  public String[] oauth2getUserInfo(String agentId, String code) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?"
        + "code=" + code
        + "&agendid=" + agentId;
    String responseText = get(url, null);
    JsonElement je = Streams.parse(new JsonReader(new StringReader(responseText)));
    JsonObject jo = je.getAsJsonObject();
    return new String[] {GsonHelper.getString(jo, "UserId"), GsonHelper.getString(jo, "DeviceId")};
  }

  @Override
  public int invite(String userId, String inviteTips) throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/invite/send";
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("userid", userId);
    if (StringUtils.isNotEmpty(inviteTips)) {
      jsonObject.addProperty("invite_tips", inviteTips);
    }
    String responseContent = post(url, jsonObject.toString());
    JsonElement tmpJsonElement = Streams.parse(new JsonReader(new StringReader(responseContent)));
    return tmpJsonElement.getAsJsonObject().get("type").getAsInt();
  }

  @Override
  public String[] getCallbackIp() throws WxErrorException {
    String url = "https://qyapi.weixin.qq.com/cgi-bin/getcallbackip";
    String responseContent = get(url, null);
    JsonElement tmpJsonElement = Streams.parse(new JsonReader(new StringReader(responseContent)));
    JsonArray jsonArray = tmpJsonElement.getAsJsonObject().get("ip_list").getAsJsonArray();
    String[] ips = new String[jsonArray.size()];
    for(int i = 0; i < jsonArray.size(); i++) {
      ips[i] = jsonArray.get(i).getAsString();
    }
    return ips;
  }

  public String get(String url, String queryParam) throws WxErrorException {
    return execute(new SimpleGetRequestExecutor(), url, queryParam);
  }

  public String post(String url, String postData) throws WxErrorException {
    return execute(new SimplePostRequestExecutor(), url, postData);
  }

  /**
   * 鍚戝井淇＄鍙戦�佽姹傦紝鍦ㄨ繖閲屾墽琛岀殑绛栫暐鏄綋鍙戠敓access_token杩囨湡鏃舵墠鍘诲埛鏂帮紝鐒跺悗閲嶆柊鎵ц璇锋眰锛岃�屼笉鏄叏灞�瀹氭椂璇锋眰
   *
   * @param executor
   * @param uri
   * @param data
   * @return
   * @throws WxErrorException
   */
  public <T, E> T execute(RequestExecutor<T, E> executor, String uri, E data) throws WxErrorException {
    int retryTimes = 0;
    do {
      try {
        return executeInternal(executor, uri, data);
      } catch (WxErrorException e) {
        WxError error = e.getError();
        /**
         * -1 绯荤粺绻佸繖, 1000ms鍚庨噸璇�
         */
        if (error.getErrorCode() == -1) {
          int sleepMillis = retrySleepMillis * (1 << retryTimes);
          try {
            //log.info("寰俊绯荤粺绻佸繖锛寋}ms 鍚庨噸璇�(绗瑊}娆�)", sleepMillis, retryTimes + 1);
            Thread.sleep(sleepMillis);
          } catch (InterruptedException e1) {
            throw new RuntimeException(e1);
          }
        } else {
          throw e;
        }
      }
    } while(++retryTimes < maxRetryTimes);

    throw new RuntimeException("寰俊鏈嶅姟绔紓甯革紝瓒呭嚭閲嶈瘯娆℃暟");
  }

  protected synchronized <T, E> T executeInternal(RequestExecutor<T, E> executor, String uri, E data) throws WxErrorException {
    if (uri.indexOf("access_token=") != -1) {
      throw new IllegalArgumentException("uri鍙傛暟涓笉鍏佽鏈塧ccess_token: " + uri);
    }
    String accessToken = getAccessToken(false);

    String uriWithAccessToken = uri;
    uriWithAccessToken += uri.indexOf('?') == -1 ? "?access_token=" + accessToken : "&access_token=" + accessToken;

    try {
      return executor.execute(getHttpclient(), httpProxy, uriWithAccessToken, data);
    } catch (WxErrorException e) {
      WxError error = e.getError();
      /*
       * 鍙戠敓浠ヤ笅鎯呭喌鏃跺皾璇曞埛鏂癮ccess_token
       * 40001 鑾峰彇access_token鏃禔ppSecret閿欒锛屾垨鑰卆ccess_token鏃犳晥
       * 42001 access_token瓒呮椂
       */
      if (error.getErrorCode() == 42001 || error.getErrorCode() == 40001) {
        // 寮哄埗璁剧疆wxCpConfigStorage瀹冪殑access token杩囨湡浜嗭紝杩欐牱鍦ㄤ笅涓�娆¤姹傞噷灏变細鍒锋柊access token
        wxCpConfigStorage.expireAccessToken();
        return execute(executor, uri, data);
      }
      if (error.getErrorCode() != 0) {
        throw new WxErrorException(error);
      }
      return null;
    } catch (ClientProtocolException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  protected CloseableHttpClient getHttpclient() {
    return httpClient;
  }

  public void setWxCpConfigStorage(WxCpConfigStorage wxConfigProvider) {
    this.wxCpConfigStorage = wxConfigProvider;

    String http_proxy_host = wxCpConfigStorage.getHttp_proxy_host();
    int http_proxy_port = wxCpConfigStorage.getHttp_proxy_port();
    String http_proxy_username = wxCpConfigStorage.getHttp_proxy_username();
    String http_proxy_password = wxCpConfigStorage.getHttp_proxy_password();

    if(StringUtils.isNotBlank(http_proxy_host)) {
      // 浣跨敤浠ｇ悊鏈嶅姟鍣�
      if(StringUtils.isNotBlank(http_proxy_username)) {
        // 闇�瑕佺敤鎴疯璇佺殑浠ｇ悊鏈嶅姟鍣�
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
            new AuthScope(http_proxy_host, http_proxy_port),
            new UsernamePasswordCredentials(http_proxy_username, http_proxy_password));
        httpClient = HttpClients
            .custom()
            .setDefaultCredentialsProvider(credsProvider)
            .build();
      } else {
        // 鏃犻渶鐢ㄦ埛璁よ瘉鐨勪唬鐞嗘湇鍔″櫒
        httpClient = HttpClients.createDefault();
      }
      httpProxy = new HttpHost(http_proxy_host, http_proxy_port);
    } else {
      httpClient = HttpClients.createDefault();
    }
  }

  @Override
  public void setRetrySleepMillis(int retrySleepMillis) {
    this.retrySleepMillis = retrySleepMillis;
  }


  @Override
  public void setMaxRetryTimes(int maxRetryTimes) {
    this.maxRetryTimes = maxRetryTimes;
  }

  @Override
  public WxSession getSession(String id) {
    if (sessionManager == null) {
      return null;
    }
    return sessionManager.getSession(id);
  }

  @Override
  public WxSession getSession(String id, boolean create) {
    if (sessionManager == null) {
      return null;
    }
    return sessionManager.getSession(id, create);
  }


  @Override
  public void setSessionManager(WxSessionManager sessionManager) {
    this.sessionManager = sessionManager;
  }
  
  @Override
  public String replaceParty(String mediaId) throws WxErrorException {
	  String url = "https://qyapi.weixin.qq.com/cgi-bin/batch/replaceparty";
	  JsonObject jsonObject = new JsonObject();
	  jsonObject.addProperty("media_id", mediaId);
	  return post(url, jsonObject.toString());
  }

  @Override
  public String replaceUser(String mediaId) throws WxErrorException {
	  String url = "https://qyapi.weixin.qq.com/cgi-bin/batch/replaceuser";
	  JsonObject jsonObject = new JsonObject();
	  jsonObject.addProperty("media_id", mediaId);
	  return post(url, jsonObject.toString());
  }

  @Override
  public String getTaskResult(String joinId) throws WxErrorException {
	  String url = "https://qyapi.weixin.qq.com/cgi-bin/batch/getresult?jobid="+joinId;
	  return get(url, null);
  }

  public File getTmpDirFile() {
    return tmpDirFile;
  }

  public void setTmpDirFile(File tmpDirFile) {
    this.tmpDirFile = tmpDirFile;
  }

  public static void main(String[] args) {
    Float a = 3.1f;
    System.out.println(3.1d);
    System.out.println(new BigDecimal(3.1d));
    System.out.println(new BigDecimal(a));
    System.out.println(a.toString());
    System.out.println(a.doubleValue());
  }
}
