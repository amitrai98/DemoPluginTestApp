alert("controller");
myapp.controller( "mycontroller", function( $scope ) {
	alert("enter controller");

	 $scope.startCalling = function(){
	 	console.log("enter");
	 	initializeVideoCalling(apiKey,sessonId,sessonToken, onSuccess, onFail) {

	 	}
	 };
	 $scope.pauseCalling = function(){

	 };
	 $scope.stopCalling = function(){

	 };
	 function onSuccess(sessonId) {
		console.log(JSON.stringify(sessonId));
	}

	function onFail(message) {
	    alert('Failed because: ' + message);
	}
})