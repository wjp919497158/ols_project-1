var userId=getQueryVariable('userId');
var page=getQueryVariable('page');
var query=getQueryVariable('query');

$(function () {
    if(query==='releasefinish'){
        $("#option1").text('已完成');
        $("#option1").val('10');
        $("#option2").text('已失效');
        $("#option2").val('11');
        $("#option3").text('已删除');
        $("#option3").val('12');
    }else if(query==='releasenotfinish'){
        $("#option1").text('审核中');
        $("#option1").val('3');
        $("#option2").text('审核未通过');
        $("#option2").val('4');
        $("#option3").text('审核通过');
        $("#option3").val('5');
    }else if(query === "acceptnotfinish"){
        $("#chooseSelect").html(
            '<option value="13">选择筛选条件</option>'+
            '<option value="1">文档类型</option>'+
            '<option value="2">图片类型</option>'+
            '<option value="14">任务分值升序</option>'+
            '<option value="15">任务分值降序</option>'+
            '<option value="16">发布时间升序</option>'+
            '<option value="17">发布时间降序</option>'+
            '<option value="18">接收时间升序</option>'+
            '<option value="19">接收时间降序</option>'
        )
    }else if(query === "acceptfinish"){
        $("#chooseSelect").html(
            '<option value="13">选择筛选条件</option>'+
            '<option value="1">文档类型</option>'+
            '<option value="2">图片类型</option>'+
            '<option value="22">已提交</option>'+
            '<option value="23">已采纳</option>'+
            '<option value="24">未采纳</option>'+
            '<option value="25">已失效</option>'+
            '<option value="14">任务分值升序</option>'+
            '<option value="15">任务分值降序</option>'+
            '<option value="20">提交时间升序</option>'+
            '<option value="21">提交时间降序</option>'
        )
    }
    layui.use(['layer', 'form','table'], function() {
        var table = layui.table;
        var layer = layui.layer;
        var form = layui.form;
        var url='';
        if(page==='releaseTask'){
            url='/user/getReleaseTaskByUserId/';
            cols=[[ //表头
                {field: 'id', title: '任务编号', align:'center',width: '10%',fixed: 'left'}
                , {field: 'name', title: '任务名称', align:'center',width: '10%'}
                , {field: 'points', title: '任务分值', align:'center',width: '10%', sort: true}
                , {field: 'state', title: '状态', align:'center',width: '10%'}
                , {field: 'type', title: '文件类型', align:'center',width: '10%', sort: true}
                , {field: 'release_time', title: '发布时间',align:'center', width: '15%', sort: true}
                , {field: 'accept_num', title: '接受者数量',align:'center', width: '10%', sort: true}
                , {field: 'ext1', title: '完成任务数量',align:'center', width: '10%', sort: true}
                , {title: '操作', align:'center',toolbar: '#barHandle'}
            ]];
        }else{
            url='/user/getAcceptTaskByUserId/';
            cols=[[ //表头
                {field: 'taskId', title: '任务编号', align:'center',width: '7%',fixed: 'left'}
                , {field: 'taskName', title: '任务名称', align:'center',width: '7%'}
                , {field: 'points', title: '任务分值', align:'center',width: '7%'}
                , {field: 'taskState', title: '任务状态', align:'center',width: '7%'}
                , {field: 'type', title: '文件类型', align:'center',width: '7%'}
                , {field: 'releaseTime', title: '发布时间',align:'center', width: '9%'}
                , {field: 'releaseName', title: '发布者',align:'center', width: '7%'}
                , {field: 'acceptNum', title: '任务接受者数量',align:'center', width: '10%'}
                , {field: 'acceptTime', title: '接受时间',align:'center', width: '9%'}
                , {field: 'acceptState', title: '接受状态',align:'center', width: '7%'}
                , {field: 'finishTime', title: '提交时间',align:'center', width: '9%'}
                , {title: '操作', align:'center',toolbar: '#barHandle'}
            ]];
        }
        var tableIns=table.render({
            elem: '#taskList'
            , height: 700
            , url: url //数据接口
            , page: true //开启分页
            , limits: [15,30,50,100]
            , limit: 15
            , method: 'get'
            , where:{
                userId:userId,
                query:query,
                queryInfo:'timeDown',
                searchInfo:''
            }
            , parseData: function(res) { //res 即为原始返回的数据
                return {
                    "code": res.meta.status, //解析接口状态
                    "msg": res.meta.msg, //解析提示文本
                    "count": res.data.total, //解析数据长度
                    "data": res.data.taskList //解析数据列表
                }
            }
            , cols: cols
            ,done: function(res, curr, count){
                if(page==='acceptTask'){
                    $(".adoptClass").hide();
                }
            }

        });
        // 监听下拉选择框
        form.on('select(chooseSelectFilter)', function (data) {
            chooesAndSearch(tableIns);
        });
        //监听工具条
        table.on('tool(monitorToolbar)', function(obj){ //注：tool 是工具条事件名，test 是 table 原始容器的属性 lay-filter="对应的值"
            var data = obj.data; //获得当前行数据
            var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）

            if(layEvent === 'check'){
                if(page==='releaseTask'){
                    checkFunc(data.id)
                }else if(page==='acceptTask'){
                    checkFunc(data.taskId)
                }
            } else if(layEvent === 'adopt'){

            }
        });
        //筛选按钮点击时间
        $("#searchButton").click(function (e) {
            e.preventDefault();
            chooesAndSearch(tableIns);
        })
    });

});

function getQueryVariable(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    // var r = window.location.search.substr(1).match(reg);
    var r = window.parent.document.getElementById("iframeMain").contentWindow.location.search.match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}

function chooesAndSearch(tableIns) {
    switch ($("#chooseSelect").val()) {
        //只执行搜索
        case '0':
            //表格重载
            tableIns.reload({
                where:{
                    queryInfo:'timeDown',
                    searchInfo:$("#searchInput").val()
                },
                page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 文档类型
        case '1':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'doc',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 图片类型
        case '2':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'img',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 审核中
        case '3':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'check',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 审核未通过
        case '4':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'notPassed',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 审核通过
        case '5':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'passed',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 发布时间升序
        case '6':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'timeUp',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 发布时间降序
        case '7':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'timeDown',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 接受者数量升序
        case '8':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'acceptNumUp',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 接受者数量降序
        case '9':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'acceptNumDown',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 已完成
        case '10':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'finish',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 已失效
        case '11':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'disable',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 已删除
        case '12':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'delete',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 默认
        case '13':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'timeDown',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 任务分值升序
        case '14':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'pointsUp',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 任务分值降序
        case '15':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'pointsDown',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 发布时间升序
        case '16':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'releaseTimeUp',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 发布时间降序
        case '17':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'releaseTimeDown',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 接收时间升序
        case '18':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'timeUp',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 接收时间降序
        case '19':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'timeDown',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 提交时间升序
        case '20':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'finishTimeUp',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 提交时间降序
        case '21':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'finishTimeDown',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 已提交
        case '22':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'finish',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 已采纳
        case '23':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'adopted',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 未采纳
        case '24':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'notAdopted',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
        // 已失效
        case '25':
            //表格重载
            tableIns.reload({
                where: { //设定异步数据接口的额外参数,可覆盖原有参数
                    queryInfo: 'invalid',
                    searchInfo:$("#searchInput").val()
                }
                ,page: {
                    curr: 1 //重新从第 1 页开始
                }
            });
            break;
    }
}
function checkFunc(taskId) {
    var page1;
    if(page==='releaseTask' ){
        if(query==='releasefinish'){
            page1 ='releaseFinishTask';
        }else {
            page1 ='releaseNotFinishTask';
        }
    }
    top.location.href="/ImageLabelTaskPage/index.html?" +
        "userId="+userId+
        "&pageType=otherReleasePage"+
        "&taskId="+taskId+
        "&pageFrom=%2FPersonalCenterPage%2Findex.html"
        +"%3FuserId%3D"+userId
        +"%26page%3D"+page1;
}