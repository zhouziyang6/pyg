//品牌服务层
app.service("brandService", function ($http) {

    //查询所有列表数据  并绑定到list对象
    this.findAll = function () {
        return $http.get("../brand/findAll.do");
    };

    //分页查询
    this.findPage = function (page, rows) {
        return $http.get("../brand/findPage.do?page=" + page + "&rows=" + rows);
    };

    //新增
    this.add = function (entity) {
        return $http.post("../brand/add.do", entity);
    };

    //修改
    this.update = function (entity) {
        return $http.post("../brand/update.do", entity);
    };

    //根据主键查询
    this.findOne = function (id) {
        return $http.get("/brand/findOne.do?id=" + id);
    };

    //批量删除
    this.delete = function (selectedIds) {
        return $http.get("../brand/delete.do?ids=" + selectedIds);
    };

    //搜索
    this.search = function (searchEntity, page, rows) {
        return $http.post("../brand/search.do?page=" + page + "&rows=" + rows, searchEntity);
    }
});