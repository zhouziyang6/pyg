//品牌控制层
app.controller("brandController", function ($scope, $controller, brandService) {

    //继承baseController
    $controller("baseController",{$scope:$scope});


    //查询所有列表数据并绑定到list对象
    $scope.findAll = function () {
        brandService.findAll().success(function (response) {
            $scope.list = response;
        });
    };


    //分页查询
    $scope.findPage = function (page, rows) {
        brandService.findPage(page, rows).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        })
    };


    //新增和修改(更新)
    $scope.save = function () {
        var obj;
        if ($scope.entity.id != null) {
            obj = brandService.update($scope.entity);
        } else {
            obj = brandService.add($scope.entity);
        }
        obj.success(function (response) {
            if (response.success) {
                //刷新列表
                $scope.reloadList();
            } else {
                alert(response.message);
            }
        })
    };


    //根据主键查询(修改)
    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity = response;
        })
    };


    //批量删除
    $scope.delete = function () {
        if ($scope.selectedIds.length < 1) {
            alert("请选择要删除的记录");
            return;
        }
        if (confirm("确定要删除选中记录吗?")) {
            brandService.delete($scope.selectedIds).success(function (response) {
                if (response.success) {
                    $scope.reloadList();
                    $scope.selectedIds = [];
                } else {
                    alert(response.message);
                }
            });
        }
    };


    //搜索
    //定义一个空的搜索对象
    $scope.searchEntity = {};
    $scope.search = function (page, rows) {
        brandService.search($scope.searchEntity, page, rows).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        })
    }
});