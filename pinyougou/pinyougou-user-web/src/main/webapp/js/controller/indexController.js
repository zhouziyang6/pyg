app.controller("indexController", function ($scope, userService) {

    $scope.getUsername = function () {
        userService.getUsername().success(function (response) {
            $scope.username = response.username;
        });
    };
});