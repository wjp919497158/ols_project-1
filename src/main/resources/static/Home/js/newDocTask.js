/**
 * 文件上传数量及其大小等控制
 * 点击开始上传按钮(uploadImgs)，执行上传
 *
 */
var success=0;
var fail=0;
var docsName = new Array();
var labelName= new Array();

var userId=getQueryVariable('userId'); //用户ID
var page = 'releaseNotFinishTask';
var query=getQueryVariable('query');

$(function (){
    layui.use(['upload','layer'],function() {
        var upload = layui.upload;
        var layer=layui.layer;

        upload.render({
            elem: '#selectDocs',
            url: '/task/uploadDocs',//Docs
            multiple: true,
            auto:false,
//			上传的单个图片大小
            size:10240,
//			最多上传的数量
            number:20,
//			MultipartFile file 对应，layui默认就是file,要改动则相应改动
            field:'file',
            bindAction: '#uploadDocs',
            accept:'file',
            exts:'txt|doc|docx|pdf',
            before: function(obj) {
                //预读本地文件示例
                obj.preview(function(index, file, result) {
                    $('#previewDocs').append('<a src="' + result
                        + '" alt="' + file.name
                        +'"height="92px" width="92px" style="margin-right: 2px">')
                });
            },
            done: function(res, index, upload) {
                //每个图片上传结束的回调，成功的话，就把新图片的名字保存起来，作为数据提交
                console.log(res);
                if(res.meta.status=="1"){
                    fail++;
                }else{
                    success++;
                    docsName.length++;
                    docsName[docsName.length-1]=res.data.docName;
                    console.log(docsName);
                }
            },
            allDone:function(obj){
                layer.msg("总共要上传文件总数为："+(fail+success)+"\n"
                    +"其中上传成功文件数为："+success+"\n"
                    +"其中上传失败文件数为："+fail
                )
            }
        });

    });
    judgeLogin();
    //清空预览图片
    cleanDocsPreview();
    //保存商品
    releaseTask();

    $("#addTag").click(function () {
        var stateJudge = true;
        if($("#labelName").val() == ""){
            alert("标注规则不可为空!");
        }else {
            for(i=0;i<labelName.length;i++){
                if(labelName[i] == $("#labelName").val()){
                    stateJudge = false;
                    break;
                }
            }
            if(stateJudge == true){
                labelName.push($("#labelName").val());
                var str = "<button type='button' class='layui-btn' value='"+$("#labelName").val()+"' onclick='removeThis(this)'>"+$("#labelName").val()+"</button>"
                $("#TagViews").append(str);
                $("#labelName").val("");
                console.log(labelName);
            }else {
                alert("此规则已存在！");
            }
        }
    })

    $("#acceptli").click(function () {
        gotoUrlByJudege("/PersonalCenterPage/index.html?userId="+userId+"&page=releaseNotFinishTask")
    })
    $("#releaseli").click(function () {
        gotoUrlByJudege("/PersonalCenterPage/index.html?userId="+userId+"&page=releaseNotFinishTask")
    })
});

function removeThis(btn) {
    btn.remove();  //删除btn元素
    for(var i=0;i<labelName.length;i++){  //删除数组元素
        if(labelName[i]===btn.value){
            labelName.splice(i,1);//从下标为i的元素开始，连续删除1个元素
            i--;//因为删除下标为i的元素后，该位置又被新的元素所占据，所以要重新检测该位置
        }
    }
}

//跳转页面要求已登陆状态的使用此方法
function gotoUrlByJudege(str) { //str为目的跳转地址
    if(userId!=null){
        $.ajax({
            url: '/user/judgeLogin',
            type: "GET",
            data: {
                "userId": userId
            },
            success: function (resultData) {
                resultData = JSON.parse(resultData);
                if(resultData.meta.status === "200"){
                    window.location.href=str;
                }else{
                    window.sessionStorage.setItem(
                        'gotoUrl',str
                    );
                    window.location.href='/Home/login.html';
                }
            }
        })
    }
    else {
        window.sessionStorage.setItem(
            'gotoUrl',str
        );
        window.location.href='/Home/login.html';//登陆完了直接跳转新任务
    }
}

/**
 * 清空预览的图片及其对应的成功失败数
 * 原因：如果已经存在预览的图片的话，再次点击上选择图片时，预览图片会不断累加
 * 表面上做上传成功的个数清0，实际后台已经上传成功保存了的，只是没有使用，以最终商品添加的提交的为准
  */
function cleanDocsPreview(){
    $("#cleanImgs").click(function(){
        success=0;
        fail=0;
        docsName.length=0;
        $('#previewImgs').html("");
        $('#imgUrls').val("");
    });
}

/**
 * 发布任务
 */
function releaseTask() {
    $('#btnSubmit').click(function () {
        var tname = $("#taskName").val();
        var tdesc = $("#taskDesc").val();
        var rpoints = $("#rewardPoints").val();
        $.ajax({
            type: "POST",
            url: "/task/creatDocTaskUrl",
            data: {
                labelName: labelName.toString(),
                originalDoc: docsName.toString(),
            },
            success: function (resultData) {
                console.log(resultData.toString());
                //url成功获取 提交表单进行数据交互
                $.ajax({
                    type: "POST",
                    url: "/task/createTask",
                    data: {
                        taskName: tname,
                        taskInfo: tdesc,
                        rewardPoints:rpoints,
                        type:0,
                        taskUrl: resultData.toString(),
                        releaseUserId: userId,
                    },
                    success: function (msg) {
                        msg=JSON.parse(msg);
                        if (msg.meta.status == "1") {
                            alert(msg.meta.msg);
                            //成功跳转界面
                           top.location.href="/TextLabelTaskPage/TextLabelTask.html?" +
                                "userId="+userId+
                                "&pageType="+'labelExamplePage'+
                                "&"+'taskId'+"="+msg.data.taskId+
                                "&pageFrom="+URLencode('/Home/Home.html')
                                +"%3FuserId%3D"+userId
                                +"%26page%3D"+'releaseNotFinishTask';
                        } else if (msg.meta.status == "0") {
                            alert(msg.meta.msg);
                        }
                    }
                });
            }
        })
    });
}
//获取URL参数
function getQueryVariable(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);//一个界面
    if (r != null&&unescape(r[2])!=='null') return unescape(r[2]);
    if(null!==window.sessionStorage.getItem(name)){
        return window.sessionStorage.getItem(name);
    }
    return null;
}
function URLencode(sStr) {
    return sStr.replace(/\%/g,"%25")
        .replace(/\+/g, '%2B')
        .replace(/\"/g,'%22')
        .replace(/\#/g,'%23')
        .replace(/\'/g, '%27')
        .replace(/\&/g, '%26')
        .replace(/\?/g, '%3F')
        .replace(/\=/g, '%3D')
        .replace(/\//g,'%2F');
}
//判断是否登录
function judgeLogin() {
    if(userId!=null){
        $.ajax({
            url: '/user/judgeLogin',
            type: "GET",
            data: {
                "userId": userId
            },
            success: function (resultData) {
                resultData = JSON.parse(resultData);
                if(resultData.meta.status === "200"){
                    var name=null;
                    $.ajax({
                        url: "/user/getUserInfo",
                        type: "get",
                        data: {
                            userId: userId
                        },
                        success: function (resultData) {
                            resultData = JSON.parse(resultData);
                            if (resultData.meta.status === "200") {
                                name = resultData.data.userInfo.name;
                                var div2=document.getElementById("logoff");
                                div2.style.display="none";
                                var div1=document.getElementById("login");
                                div1.style.display="block";
                                var li1=document.getElementById("acceptli");
                                li1.style.visibility="visible";
                                var li2=document.getElementById("releaseli");
                                li2.style.visibility="visible"; //这样做布局没问题了，但是存在BUG 可以前端修改显示出来。所以点击事件需要判断登录状态。
                                var a=document.getElementById("userName");
                                a.innerText=name;
                                a.href="/PersonalCenterPage/index.html?userId="+userId+"&page=personalInfo";

                            }
                        }
                    });

                }else{
                    sessionStorage.clear();   //清除所有session值
                    window.location.href='/Home/Home.html';

                }

            }
        })
    }
}
//注销
function cancel() {
    sessionStorage.clear();   //清除所有session值
    window.location.href='/Home/Home.html';
}