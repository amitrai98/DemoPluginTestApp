
var myApp = angular.module('myApp', []);
myApp.controller( "myController", function($scope) {
	
	 $scope.muteText = 'Mute';
	 var flag = false;
	 $scope.startCalling = function(){
	 	console.log("enter");
                 var apiKey = '45598312';
                 var sessionId = '1_MX40NTU5ODMxMn5-MTQ2NzI2NDExMTI2M35qWEtkVlE0Q2xaUHl2anYvS2d3emowbGZ-fg';
                 var token = 'T1==cGFydG5lcl9pZD00NTU5ODMxMiZzaWc9MTMxM2ZlN2I1MDc4ZjcyNTg5NTYxYmEwYjgyNWQxZjNlNWI2NzFjNjpzZXNzaW9uX2lkPTFfTVg0ME5UVTVPRE14TW41LU1UUTJOekkyTkRFeE1USTJNMzVxV0V0a1ZsRTBRMnhhVUhsMmFuWXZTMmQzZW1vd2JHWi1mZyZjcmVhdGVfdGltZT0xNDY3MjY0MTIyJm5vbmNlPTAuODYwNzgzMDI2MzIwODU5OCZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNDY5ODU2MTIy';
                 var obj = '{'
                 +'"ApiKey":"45598312",'
                 +'"SessionId":"1_MX40NTU5ODMxMn5-MTQ2NzI2NDExMTI2M35qWEtkVlE0Q2xaUHl2anYvS2d3emowbGZ-fg",'
                 +'"Token":"T1==cGFydG5lcl9pZD00NTU5ODMxMiZzaWc9MTMxM2ZlN2I1MDc4ZjcyNTg5NTYxYmEwYjgyNWQxZjNlNWI2NzFjNjpzZXNzaW9uX2lkPTFfTVg0ME5UVTVPRE14TW41LU1UUTJOekkyTkRFeE1USTJNMzVxV0V0a1ZsRTBRMnhhVUhsMmFuWXZTMmQzZW1vd2JHWi1mZyZjcmVhdGVfdGltZT0xNDY3MjY0MTIyJm5vbmNlPTAuODYwNzgzMDI2MzIwODU5OCZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNDY5ODU2MTIy",'
                 +'"UserType":"Pro",'
                 +'"IsAbleToCall":"false",'
                 +'"ProfileImage":"http://igert2012.videohall.com/images/defaults/small_default_profile.png",'
                 +'"CallPerMinute":"2",'
                 +'"Amount":"22.30"'
                 +'}';
                 var a = JSON.parse(obj);
                 console.log("parseee :" + jsonObj);
                 var jsonObj = JSON.stringify(eval("(" + obj + ")"));
                 console.log("jkahsdjkah = " + jsonObj);
        VideoPlugin.initializeVideoCalling(jsonObj, onSuccess, onFail);
	 	console.log("video");
//	 		hello.startMakingConnection();
                 setTimeout(myFunction, 15000);
	 };
                 function myFunction() {
                 alert('Hello');
                 VideoPlugin.showLowBalanceWarning("30s");
                 }
	 $scope.muteCalling = function(){
	 	console.log("enter mute calling");
	 	hello.showLowBalanceWarning(jsonObj, onSuccess, onFail);
	 	if(flag == false){
	 		$scope.muteText = 'Unmute';
	 		flag = true;
	 	}else{
	 		$scope.muteText = 'Mute';
	 		flag = false;
	 	}
	 };
	 $scope.stopCalling = function(){
	 	console.log("enter stop calling");
	 	hello.endCalling("testing Here");
	 };
                 
    $scope.SubscriberToAudio = function(){
        console.log("subscriberAudio");
        hello.switchSubscriberToAudioCallOnly();
    };
    $scope.publisherToAudio = function(){
        console.log("PublisherAudio");
        hello.switchPublisherToAudioCallOnly();
    };
	 function onSuccess(sessonId) {
                 
                 console.log("Success : " + JSON.stringify(sessonId));
	}

	function onFail(message) {
	    console.log("Failed : " + JSON.stringify(message));
	}
});