app.controller("searchController", function ($scope, $location, searchService) {

    //搜索条件对象
    $scope.searchMap = {
        "keywords": "",
        "category": "",
        "brand": "",
        "spec": {},
        "price": "",
        "pageNo": 1,
        "pageSize": 20,
        "sortField": "",
        "sort": ""
    };

    //搜索
    $scope.search = function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap = response;
            //构建分页导航条信息
            buildPageInfo();
        });
    };

    //添加过滤条件
    $scope.addSearchItem = function (key, value) {
        if ("brand" == key || "category" == key || "price" == key) {
            //如果点击的是品牌或者分类的话
            $scope.searchMap[key] = value;

        } else {
            $scope.searchMap.spec[key] = value
        }
        $scope.searchMap.pageNo = 1;
        //点击过滤条件后需要重新搜索
        $scope.search();
    }

    //删除过滤条件
    $scope.removeSearchItem = function (key) {
        if ("brand" == key || "category" == key || "price" == key) {
            //如果点击的是品牌或者分类的话
            $scope.searchMap[key] = "";
        } else {
            //规格
            delete $scope.searchMap.spec[key];
        }
        $scope.searchMap.pageNo = 1;
        //重新搜索
        $scope.search();
    }

    buildPageInfo = function () {
        //定义要在页面显示的页号的集合
        $scope.pageNoList = [];

        //定义要在页面上显示的页号的数量
        var showPageNoTotal = 5;

        //起始页号
        var startPageNo = 1;

        //结束页号
        var endPageNo = $scope.resultMap.totalPages;

        //如果总页数大于要显示的页数才有需要处理显示页号数,否则直接显示所有页号
        // 导航条要显示的页数是5页,如果要显示的页数大于5页了 就执行if里面的代码
        //                    944                5
        if ($scope.resultMap.totalPages > showPageNoTotal) {

            //计算当前页左右间隔页数  向下取整
            var interval = Math.floor(showPageNoTotal / 2);// 上一页...4 5 6 7 8 9...下一页

            //根据间隔得出起始,结束页号
            //                                当前页 6
            startPageNo = parseInt($scope.searchMap.pageNo) - interval
            endPageNo = parseInt($scope.searchMap.pageNo) + interval

            //处理页号越界
            if (startPageNo > 0) {                             //       10  -  5   +   1
                //如果结束页号是大于总页数的则都设置为总页数,起始页号就要 总页数-要显示的页数+1
                if (endPageNo > $scope.resultMap.totalPages) {
                    startPageNo = $scope.resultMap.totalPages - showPageNoTotal + 1;//开始页号就是总页数-要显示的页数+1; 10-5+1
                    endPageNo = $scope.resultMap.totalPages; //如果大于要显示的页数的话 结束页号 就是返回结果的页数
                }
            } else {
                //如果起始页号是小于1的则都设置为1,结束页号就为 要显示的总页数号(5)
                startPageNo = 1;
                endPageNo = showPageNoTotal;
            }
        }

        //前面三个点:起始页号大于1则存在
        $scope.frontDot = false;
        if (startPageNo > 1) {
            $scope.frontDot = true;
        }
        //后面三个点:结束页号小于总页数则存在
        $scope.backDot = false;
        if (endPageNo < $scope.resultMap.totalPages) {
            $scope.backDot = true;

        }

        //获得正确的页号  遍历显示在页面上
        for (var i = startPageNo; i <= endPageNo; i++) {
            $scope.pageNoList.push(i)

        }
    }

    //判断是否是当前页
    $scope.isCurrentPage = function (pageNo) {
        return $scope.searchMap.pageNo == pageNo;
    }

    //跳转到某个页
    $scope.queryByPage = function (pageNo) {
        pageNo = parseInt(pageNo);
        if (pageNo > 0 && pageNo <= $scope.resultMap.totalPages) {
            $scope.searchMap.pageNo = pageNo;//当前页等于传递的页号
            $scope.search();//刷新
        }
    }

    //排序搜索
    $scope.sortSearch = function (sortField, sort) {
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.sort = sort;
        $scope.search();
    }

    //加载搜索关键字并搜索
    $scope.loadKeywords = function () {
        $scope.searchMap.keywords = $location.search()["keywords"];
        $scope.search();
    }
});
