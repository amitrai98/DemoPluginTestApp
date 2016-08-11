var myApp = angular.module('myApp', []);
myApp.controller("myController", function($scope) {
    $scope.muteText = 'Mute';
    var flag = false;
    $scope.startCalling = function() {
        console.log("enter");
        var apiKey = '45635122';
        var sessionId = '2_MX40NTYzNTEyMn5-MTQ3MDgwMzM2NDQxNH5WbjRMU3h5eCthSkUyNnQzUk1zUnkyQkR-fg';
        var token = 'T1==cGFydG5lcl9pZD00NTYzNTEyMiZzaWc9ZmViZmMwZTcyMmM4NDBiMDFmYzg4OGI1OThjOTAxZjMwOTNjMjc5MDpzZXNzaW9uX2lkPTJfTVg0ME5UWXpOVEV5TW41LU1UUTNNRGd3TXpNMk5EUXhOSDVXYmpSTVUzaDVlQ3RoU2tVeU5uUXpVazF6VW5reVFrUi1mZyZjcmVhdGVfdGltZT0xNDcwODAzMzk1Jm5vbmNlPTAuMDY2NzI4ODYzMzk5NDc1ODEmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTQ3MzM5NTM5NA==';
        var obj = '{'
        +'"ApiKey":"45635122",'
        +'"SessionId":"2_MX40NTYzNTEyMn5-MTQ3MDgwMzM2NDQxNH5WbjRMU3h5eCthSkUyNnQzUk1zUnkyQkR-fg",'
        +'"Token":"T1==cGFydG5lcl9pZD00NTYzNTEyMiZzaWc9ZmViZmMwZTcyMmM4NDBiMDFmYzg4OGI1OThjOTAxZjMwOTNjMjc5MDpzZXNzaW9uX2lkPTJfTVg0ME5UWXpOVEV5TW41LU1UUTNNRGd3TXpNMk5EUXhOSDVXYmpSTVUzaDVlQ3RoU2tVeU5uUXpVazF6VW5reVFrUi1mZyZjcmVhdGVfdGltZT0xNDcwODAzMzk1Jm5vbmNlPTAuMDY2NzI4ODYzMzk5NDc1ODEmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTQ3MzM5NTM5NA==",'
        +'"UserType":"Pro",'
        +'"IsAbleToCall":"false",'
        +'"ProfileImage":"http://igert2012.videohall.com/images/defaults/small_default_profile.png",'
        +'"UserName":"Abella",'
        +'"CallPerMinute":"2",'
        +'"Amount":"22.30"'
        +'}';
        var a = JSON.parse(obj);
        console.log("parseee :" + jsonObj);
        var jsonObj = JSON.stringify(eval("(" + obj + ")"));
        console.log("jkahsdjkah = " + jsonObj);

        VideoPlugin.initializeVideoCalling(jsonObj, onSuccess, onFail);
        console.log("video");
        //          hello.startMakingConnection();
        setTimeout(myFunction3, 3000);
    };

//    function myFunction() {
//        VideoPlugin.showLowBalanceWarning("30s", function() {
//            console.log('success==>');
//			setTimeout(myFunction3, 3000);
//        }, function() {
//            console.log('error==>');
//            setTimeout(myFunction3, 3000);
//        });
//    }
//    function myFunction2() {
//            VideoPlugin.receivedResponseFromAPI("credit", "success", "0", function() {
//                console.log('success==>');
//    			setTimeout(myFunction3, 3000);
//            }, function() {
//                console.log('error==>');
//                setTimeout(myFunction3, 3000);
//            });
//        }

	function myFunction3() {
			VideoPlugin.getUserBalance(30, function() {
				console.log('success==>');
	//			setTimeout(myFunction, 3000);
			}, function() {
				console.log('error==>');
			});
		}



    $scope.muteCalling = function() {
        console.log("enter mute calling");
        hello.showLowBalanceWarning(jsonObj, onSuccess, onFail);
        if (flag == false) {
            $scope.muteText = 'Unmute';
            flag = true;
        } else {
            $scope.muteText = 'Mute';
            flag = false;
        }
    };
    $scope.stopCalling = function() {
        console.log("enter stop calling");
        hello.endCalling("testing Here");
    };

    $scope.SubscriberToAudio = function() {
        console.log("subscriberAudio");
        hello.switchSubscriberToAudioCallOnly();
    };
    $scope.publisherToAudio = function() {
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