function query()
{
$('#table').bootstrapTable('filterBy', {c0: ["盒马"]},
 {
 'filterAlgorithm': function(row,filters)
  {
	 var val =row["c0"];
	 if(val == undefined)
		 return false;
     if(val.indexOf("盒马") != -1) 
    	 return true;
  }
  });
}