app.controller("baseController", function ($scope) {

    //初始化分页参数
    $scope.paginationConf = {
        currentPage: 1,//当前页号
        itemsPerPage: 10,//页大小
        totalItems: 0,//总记录数
        perPageOptions: [10, 20, 30, 40, 50],//可选择的每页大小
        onChange: function () {//当上述参数发生了变化后触发,重新加载
            $scope.reloadList();
        }
    };

    //重新加载
    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage)
    };

    //定义一个放置选择的id的数组,复选框 (批量删除)
    $scope.selectedIds = [];
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {
            $scope.selectedIds.push(id);
        } else {
            var index = $scope.selectedIds.indexOf(id);
            //删除位置,删除个数
            $scope.selectedIds.splice(index, 1);
        }
    };

});