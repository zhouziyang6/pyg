app.service("loginService", function ($http) {
    //主界面显示登录名
    this.getUsername = function () {
        return $http.get("../login/getUsername.do");
    };
});