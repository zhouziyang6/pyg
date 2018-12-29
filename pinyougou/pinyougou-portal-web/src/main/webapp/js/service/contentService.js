app.service("contentService", function ($http) {
    //根据分类 id 查询分类对应的内容列表
    this.findContentListByCategoryId = function (categoryId) {
        return $http.get("content/findContentListByCategoryId.do?categoryId="+categoryId);
    };
});