app.controller("userController", function ($scope, $controller, userService) {

    $scope.entity = {"username":"", "password":"", "phone":""};
    $scope.register = function () {
        if ($scope.entity.username == "") {
            alert("请输入用户名");
            return;
        }
        if ($scope.entity.password == "") {
            alert("请输入密码");
            return;
        }
        //判断两次密码是否一致
        if($scope.password != $scope.entity.password){
            alert("两次输入的密码不一致;请重新输入");
            return;
        }

        if ($scope.entity.phone == "") {
            alert("请输入手机号");
            return;
        }

        userService.register($scope.entity, $scope.smsCode).success(function (response) {
            alert(response.message);
        });
    };

    $scope.entity = {};
    $scope.sendSmsCode = function () {
        if($scope.entity.phone == null || $scope.entity.phone=="") {
            alert("请输入手机号");
            return;
        }

        userService.sendSmsCode($scope.entity.phone).success(function (response) {
            alert(response.message);
        });
    };
});