var zTreeObj;
var setting = {
    view: {
        addHoverDom: addHoverDom,
        removeHoverDom: removeHoverDom,
        selectedMulti: false
    },
    edit: {
        drag: {
            autoExpandTrigger: true,
            prev: dropPrev,
            inner: dropInner,
            next: dropNext
        },
        enable: true,
        editNameSelectAll: true,
        showRemoveBtn: showRemoveBtn,
        showRenameBtn: showRenameBtn
    },
    data: {
        simpleData: {
            enable: true
        }
    },
    callback: {
        onClick: onClick,
        onRightClick: OnRightClick,
        beforeDrag: beforeDrag,
        beforeDrop: beforeDrop,
        beforeDragOpen: beforeDragOpen,
        onDrag: onDrag,
        onDrop: onDrop,
        onExpand: onExpand,
        beforeEditName: beforeEditName,
        beforeRemove: beforeRemove,
        beforeRename: beforeRename,
        onRemove: onRemove,
        onRename: onRename
    }
};

// zTree 的数据属性，深入使用请参考 API 文档（zTreeNode 节点数据详解）
var zNodes;

function onClick(event, treeId, treeNode) {
    $("#iconType").val(zTree.getSelectedNodes()[0].iconType);
    $("#iconName").val(zTree.getSelectedNodes()[0].iconName);
    $("#iconType").change(function () {
        zTree.getSelectedNodes()[0].iconType = $("#iconType").val();
    });
    $("#iconName").blur(function () {
        zTree.getSelectedNodes()[0].iconName = $("#iconName").val();
    })
    if(treeNode["children"]){
        if( treeNode["children"].length>0){
            $("#UrlTable").css({"visibility": "hidden"});
        }else{
            $("#UrlTable").css({"visibility": "visible"});
        }
    }else{
        $("#UrlTable").css({"visibility": "visible"});
    }
    $("#selectedItemURL").val(treeNode.url);
    $("#iconName").val(treeNode.iconName);
    $("#iconType").val(treeNode.iconType);
}
var curDragNodes;
function beforeDrag(treeId, treeNodes) {
    for (var i=0,l=treeNodes.length; i<l; i++) {
        if (treeNodes[i].drag === false) {
            curDragNodes = null;
            return false;
        } else if (treeNodes[i].parentTId && treeNodes[i].getParentNode().childDrag === false) {
            curDragNodes = null;
            return false;
        }
    }
    curDragNodes = treeNodes;
    return true;
}

function dropPrev(treeId, nodes, targetNode) {
    var pNode = targetNode.getParentNode();
    if (pNode && pNode.dropInner === false) {
        return false;
    } else {
        for (var i=0,l=curDragNodes.length; i<l; i++) {
            var curPNode = curDragNodes[i].getParentNode();
            if (curPNode && curPNode !== targetNode.getParentNode() && curPNode.childOuter === false) {
                return false;
            }
        }
    }
    return true;
}

function dropInner(treeId, nodes, targetNode) {
    if (targetNode && targetNode.dropInner === false) {
        return false;
    } else {
        for (var i=0,l=curDragNodes.length; i<l; i++) {
            if (!targetNode && curDragNodes[i].dropRoot === false) {
                return false;
            } else if (curDragNodes[i].parentTId && curDragNodes[i].getParentNode() !== targetNode && curDragNodes[i].getParentNode().childOuter === false) {
                return false;
            }
        }
    }
    return true;
}
function dropNext(treeId, nodes, targetNode) {
    var pNode = targetNode.getParentNode();
    if (pNode && pNode.dropInner === false) {
        return false;
    } else {
        for (var i=0,l=curDragNodes.length; i<l; i++) {
            var curPNode = curDragNodes[i].getParentNode();
            if (curPNode && curPNode !== targetNode.getParentNode() && curPNode.childOuter === false) {
                return false;
            }
        }
    }
    return true;
}

function afterDrop(treeNode) {
    if(treeNode.getParentNode()){
        treeNode["pid"] = treeNode.getParentNode()["id"];
        var childs = treeNode.getParentNode()["children"];
        for(var i=0; i<childs.length; i++){
            childs[i]["index_of_brother"] = i;
        }
    }else{
        treeNode["pid"] = "0";
        var rootNodes = zTreeObj.getNodesByParam("pid", "0", null);
        for(var i=0; i<rootNodes.length; i++){
            rootNodes[i]["index_of_brother"] = i;
        }
    }
}

function beforeDragOpen(treeId, treeNode) {
    autoExpandNode = treeNode;
    return true;
}
function beforeDrop(treeId, treeNodes, targetNode, moveType, isCopy) {
    return true;
}
function onDrag(event, treeId, treeNodes) {
}
function onDrop(event, treeId, treeNodes, targetNode, moveType, isCopy) {
    afterDrop(treeNodes[0]);
}
function onExpand(event, treeId, treeNode) {
}

function beforeEditName(treeId, treeNode) {
    zTree.selectNode(treeNode);
    zTree.editName(treeNode);
    return false;
}
function beforeRemove(treeId, treeNode) {
    var zTree = $.fn.zTree.getZTreeObj("treeDemo");
    zTree.selectNode(treeNode);
    return confirm("确认删除 节点 -- " + treeNode.name + " 吗？");
}
function onRemove(e, treeId, treeNode) {
}
function beforeRename(treeId, treeNode, newName, isCancel) {
    if (newName.trim().length == 0) {
        setTimeout(function() {
            var zTree = $.fn.zTree.getZTreeObj("treeDemo");
            zTree.cancelEditName();
            alert("节点名称不能为空.");
        }, 0);
        return false;
    }
    return true;
}
function onRename(e, treeId, treeNode, isCancel) {
}
function showRemoveBtn(treeId, treeNode) {
    return true;
}
function showRenameBtn(treeId, treeNode) {
    return true;
}

var newCount = 1;
function addHoverDom(treeId, treeNode) {
    var sObj = $("#" + treeNode.tId + "_span");
    if (treeNode.editNameFlag || $("#addBtn_"+treeNode.tId).length>0) return;
    var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
        + "' title='add node' onfocus='this.blur();'></span>";
    sObj.after(addStr);
    var btn = $("#addBtn_"+treeNode.tId);
    if (btn) btn.bind("click", function(){
        var zTree = $.fn.zTree.getZTreeObj("treeDemo");
        if(treeNode.getParentNode()){
            if(treeNode.getParentNode().getParentNode()){
                alert("不能有4级节点");
                return;
            }
        }
        var nowAll = transformJSONtoeachJSON();
        var biggestOfNowAll = $("#appId").text()*1000;
        for(var i=0;i<nowAll.length;i++){
            var id = nowAll[i].id;
            if(id > biggestOfNowAll){
                biggestOfNowAll = id;
            }

        }
        biggestOfNowAll += 1;
        var pid = 0;
        if(treeNode.getParentNode()){
            pid = treeNode.getParentNode().id;
        }

        zTree.addNodes(treeNode, { id:biggestOfNowAll, pid:pid,name:"增加" + (addCount++), app_id:$("#appId").text(), url:"#", index_of_brother:0});
        var nNode = zTreeObj.getNodesByParam("id",biggestOfNowAll,null)[0];
        zTree.editName(nNode);
        return false;
    });
}
function removeHoverDom(treeId, treeNode) {
    $("#addBtn_"+treeNode.tId).unbind().remove();
}

function OnRightClick(event, treeId, treeNode) {
    if (!treeNode && event.target.tagName.toLowerCase() != "button" && $(event.target).parents("a").length == 0) {
        zTree.cancelSelectedNode();
        showRMenu("root", event.clientX, event.clientY);
    } else if (treeNode && !treeNode.noR) {
        zTree.selectNode(treeNode);
        showRMenu("node", event.clientX, event.clientY);
    }
}

function showRMenu(type, x, y) {
    $("#rMenu ul").show();
    if (type=="root") {
        $("#m_del").hide();
        $("#m_check").hide();
        $("#m_unCheck").hide();
    } else {
        $("#m_del").show();
        $("#m_check").show();
        $("#m_unCheck").show();
    }

    y += document.body.scrollTop;
    x += document.body.scrollLeft;
    rMenu.css({"top":y+"px", "left":x+"px", "visibility":"visible"});

    $("body").bind("mousedown", onBodyMouseDown);
}
function hideRMenu() {
    if (rMenu) rMenu.css({"visibility": "hidden"});
    $("body").unbind("mousedown", onBodyMouseDown);
}
function onBodyMouseDown(event){
    if (!(event.target.id == "rMenu" || $(event.target).parents("#rMenu").length>0)) {
        rMenu.css({"visibility" : "hidden"});
    }
}
var addCount = 1;
function addTreeNode() {
    hideRMenu();
    var nowAll = transformJSONtoeachJSON();
    var biggestOfNowAll = $("#appId").text()*1000;
    for(var i=0;i<nowAll.length;i++){
        var id = nowAll[i].id;
        if(id > biggestOfNowAll){
            biggestOfNowAll = id;
        }
    }
    biggestOfNowAll += 1;
    var newNode;
    if (zTree.getSelectedNodes()[0]) {
         newNode= { id:biggestOfNowAll, pid:zTree.getSelectedNodes()[0].id,name:"增加" + (addCount++), app_id:$("#appId").text(), url:"#", index_of_brother:0};
    }else {
        newNode = { id:biggestOfNowAll, pid:0,name:"增加" + (addCount++),app_id:$("#appId").text(), url:"#", index_of_brother:0};

    }
    if (zTree.getSelectedNodes()[0]) {
        if(zTree.getSelectedNodes()[0].getParentNode()){
            if(zTree.getSelectedNodes()[0].getParentNode().getParentNode()){
                alert("不能有4级节点");
                return ;
            }
        }
        zTree.addNodes(zTree.getSelectedNodes()[0], newNode);
    } else {
        zTree.addNodes(null, newNode);
    }
    var nNode = zTreeObj.getNodesByParam("id",biggestOfNowAll,null)[0];
    zTree.editName(nNode);
    afterDrop(nNode);
}

function removeTreeNode() {
    hideRMenu();
    var nodes = zTree.getSelectedNodes();
    if (nodes && nodes.length>0) {
        if (nodes[0].children && nodes[0].children.length > 0) {
            var msg = "要删除的节点是父节点，如果删除将连同子节点一起删掉。\n\n请确认！";
            if (confirm(msg)==true){
                zTree.removeNode(nodes[0]);
            }
        } else {
            zTree.removeNode(nodes[0]);
        }
    }
}

function resetTree() {
    hideRMenu();
    $.fn.zTree.init($("#treeDemo"), setting, zNodes);
}

function getExtractzTreeJSON() {

    var nodes = JSON.parse(JSON.stringify(zTreeObj.getNodes()));
    for(var k=0;k<nodes.length;k++){
        nodes[k] = delWastedParams(nodes[k]);
        var child = nodes[k]["children"];
        if(child && child.length > 0){
            childHUIDIAO(nodes[k]);
        }
    }
    return nodes;
}

function childHUIDIAO(fatherNode) {
    var children = fatherNode["children"];
    for(var k=0;k<children.length;k++){
        children[k] = delWastedParams(children[k]);
        if(children[k]["children"] && children[k]["children"].length > 0){
            childHUIDIAO(children[k]);
        }
    }
    return children;
}

function delWastedParams(nodes) {
    delete nodes["level"];
    delete nodes["tId"];
    delete nodes["parentTId"];
    delete nodes["open"];
    delete nodes["isParent"];
    delete nodes["zAsync"];
    delete nodes["isFirstNode"];
    delete nodes["isLastNode"];
    delete nodes["isAjaxing"];
    delete nodes["checked"];
    delete nodes["checkedOld"];
    delete nodes["nocheck"];
    delete nodes["chkDisabled"];
    delete nodes["halfCheck"];
    delete nodes["check_Child_State"];
    delete nodes["check_Focus"];
    delete nodes["isHover"];
    delete nodes["editNameFlag"];
    delete nodes["pId"];
    return nodes;
}

function renameURL() {
    var selectedNode = zTree.getSelectedNodes()[0];
    if(!$("#selectedItemURL").val()){
        $("#selectedItemURL").val(" ")
    }
    selectedNode.url = $("#selectedItemURL").val();

}


function transformJSONtoeachJSON() {
    var submitData = JSON.parse(JSON.stringify(getExtractzTreeJSON()));
    //转换成Node格式的JSON
    var submitDataList = [];
    for(var i=0; i<submitData.length; i++){
        var one = JSON.parse(JSON.stringify(submitData[i]));
        if(one["children"]){
            var oneChildren = one["children"];
            delete one["children"];
            submitDataList.push(one);
            submitDataList = addChildtoSubmitList(submitDataList, oneChildren);
        }else{
            submitDataList.push(one);
        }
    }
    return submitDataList;
}

function addChildtoSubmitList(submitDataList, oneChildren) {
    for (var j=0; j<oneChildren.length; j++){
        var secondLevel = oneChildren[j]["children"];
        delete oneChildren[j]["children"];
        submitDataList.push(oneChildren[j]);
        if(secondLevel){
            for (var k=0; k<secondLevel.length; k++){
                submitDataList.push(secondLevel[k]);
            }
        }
    }
    return submitDataList;
}

function submitMenu() {
    var submitDataList = transformJSONtoeachJSON();
    $.ajax({
        type: 'POST',
        url: "/api/submit-menu?appId=" + $("#appId").text(),
        contentType: "application/json", //必须有
        data: JSON.stringify(submitDataList),
        success: function(data){
            alert("完成提交");
        },
        error: function (err) {
            alert("提交失败");
        }
    });
}

var zTree, rMenu;
$(document).ready(function(){
    // 从 URL 的 appId 属性获取需要管理的菜单
    var appId = (location.search.split('appId=')[1] || '').split('&')[0];
    $("#appId").text(appId);

    $.ajax({
        type: 'GET',
        dataType:'json',
        url: "/api/get-menu?appId=" + appId,
        success: function(data){
            zNodes = data;
            zTreeObj = $.fn.zTree.init($("#treeDemo"), setting, zNodes);
            zTree = $.fn.zTree.getZTreeObj("treeDemo");
            rMenu = $("#rMenu");
        },
        error: function (err) {
            console.log("读取出错");
        }
    })
});
