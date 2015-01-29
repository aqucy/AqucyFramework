<#assign tdCount=0>
<#assign inputStyle="style='width:110px'">
<#assign validate="">
<#assign subTableDialogHeight="">
<#if tableConfig.subViewObjectId gt 0>
    <#assign subTableDialogHeight="height:80%;">
</#if>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE8">
    <title></title>
    <link rel="stylesheet" type="text/css" href="/js/easyui/themes/gray/easyui.css"/>
    <link rel="stylesheet" type="text/css" href="/js/easyui/themes/icon.css"/>
    <link rel="stylesheet" type="text/css" href="/js/easyui/themes/color.css"/>
    <script type="text/javascript" src="/js/easyui/jquery-1.11.2.min.js"></script>
    <script type="text/javascript" src="/js/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="/js/easyui/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript" src="/js/acyFramework/easyui_validate_ext.js"></script>
    <script type="text/javascript" src="/js/acyFramework/aqucy.js"></script>
    <link rel="stylesheet" type="text/css" href="/css/icon.css"/>
    <style type="text/css">
        .tdRightPadding {
            padding-right: 20px;
            width: 170px;
            text-align: left;
        }

    </style>
    <script type="text/javascript">
        $.parser.auto = false;
        var del, subDel;
        var init = true;
        function showCombboxData(itemId,code){
           var data = $("#"+itemId).combobox("getData");
            if(data==null||data==""){
                $("#"+itemId).combobox("reload","/getDict?code="+code);
            }
        }
        $.parser.onComplete = function (context) {
            $("#Loading").css("display","none");
        };
        $(document).ready(function () {
            /////////////////////////字典匹配////////////////////////////////////////////////////
        <#if tableConfig.dicts?exists>
            <#list  tableConfig.dicts?keys as key>
                function render_${acy[key]}(value) {
                    <#list  tableConfig.dicts[key] as obj>
                        if(value=='${obj.value}'){
                            return "${obj.label}";
                        }
                    </#list>
                }

            </#list>
        </#if>
                function createDataGrid() {
                var initUrl = '/project/${tableConfig.tableAliasName}?op=view';
                initUrl = encodeURI(initUrl);
                $('#dg').datagrid(
                        {

                            url: initUrl,
                            idField: 'id',
                            title: '学生作业',
                            fit: true,
                            fitColumns: true,
                            pageSize: 10,
                            pagination: true,
                            singleSelect: false,
                            selectOnCheck: true,
                            sortName: 'id',
                            pageList: [10, 30, 50, 100,300,500,800,1000],
                            sortOrder: 'desc',
                            ctrlSelect: true,
                            rownumbers: true,
                            showFooter: true,
                            frozenColumns: [[]],
                            columns: [
                                [

                                    {
                                        field: 'id',
                                        title: '主键',
                                        hidden: false,
                                        checkbox: true,
                                        sortable: true,
                                        width: 120
                                    },
                                <#list tableConfig.allDisplayPojos as pojo>
                                    {
                                        field: '${pojo.columnName}',
                                        title: '${pojo.displayName}',
                                        <#if pojo.dictCode??>
                                            formatter: render_${acy[pojo.dictCode]},
                                        </#if>
                                        sortable: true,
                                        width: 120
                                    },
                                </#list>
                                    {
                                        field: 'opt', title: '操作', width: 200, formatter: function (value, rec, index) {
                                        if (!rec.id) {
                                            return '';
                                        }
                                        var href = "<a href='javascript:' title='删除' onclick='del(" + rec.id + ")'><img src='/icons/delete.png'/></a>";
                                        return href;
                                    }
                                    }
                                ]
                            ],
                            onLoadSuccess: function (data) {
                                $("#dg").datagrid("clearSelections");
                            },
                            onClickRow: function (rowIndex, rowData) {
                                rowid = rowData.id;
                                gridname = 'dg';
                            },
                            onDblClickRow:function(index,row){
                                view();
                            }
                        });
            }
        <#if tableConfig.subViewObjectId gt 0>
            function createSubDataGrid(pvalue) {
                var initUrl = '/project/addasShop?op=view&addasShop.managerUserId=' + pvalue;
                initUrl = encodeURI(initUrl);
                $('#subdg').datagrid(
                        {

                            url: initUrl,
                            idField: 'id',
                            title: '学生作业',
                            fit: true,
                            fitColumns: true,
                            pageSize: 10,
                            pagination: true,
                            singleSelect: false,
                            selectOnCheck: true,
                            sortName: 'id',
                            pageList: [10, 30, 50, 100],
                            sortOrder: 'desc',
                            ctrlSelect: true,
                            rownumbers: true,
                            showFooter: true,
                            frozenColumns: [[]],
                            columns: [
                                [

                                    {
                                        field: 'id',
                                        title: '主键',
                                        hidden: false,
                                        checkbox: true,
                                        sortable: true,
                                        width: 120
                                    },
                                    {
                                        field: 'shopName',
                                        title: '店铺名称',
                                        sortable: true,
                                        width: 120
                                    },
                                    {
                                        field: 'openDate',
                                        title: '营业时间',
                                        hidden: false,
                                        sortable: true,
                                        width: 120
                                    },
                                    {
                                        field: 'status',
                                        title: '状态',
                                        formatter: renderStatus,
                                        hidden: false,
                                        sortable: true,
                                        width: 120
                                    },
                                    {
                                        field: 'age',
                                        title: '店龄',
                                        hidden: false,
                                        sortable: true,
                                        width: 120
                                    },
                                    {
                                        field: 'address',
                                        title: '店铺地址',
                                        sortable: true,
                                        width: 120
                                    },
                                    {
                                        field: 'opt', title: '操作', width: 200, formatter: function (value, rec, index) {
                                        if (!rec.id) {
                                            return '';
                                        }
                                        var href = "<a href='javascript:' title='删除' onclick='subDel(" + rec.id + ")'><img src='/icons/delete.png'/></a>";
                                        return href;
                                    }
                                    }
                                ]
                            ],
                            onLoadSuccess: function (data) {
                                $("sub#dg").datagrid("clearSelections");
                            },
                            onClickRow: function (rowIndex, rowData) {
                                rowid = rowData.id;
                                gridname = 'subdg';
                            },
                            onDblClickRow:function(index,row){
                                $("#subView").trigger("click");
                            }
                        });
            }
        </#if>
            function createPopDataGrid(code,callback) {
                var url = "/systemManager/userManager?op=popGrid&popGridCode="+code;
                var init = true;
                $('#popdg').datagrid(
                        {
                            url:url,
                            idField: 'id',
                            fit: true,
                            fitColumns: true,
                            pageSize: 10,
                            callback:callback,
                            pagination: true,
                            singleSelect: false,
                            selectOnCheck: true,
//                            sortName: 'id',
                            pageList: [10, 30, 50, 100],
//                            sortOrder: 'desc',
                            ctrlSelect: true,
                            rownumbers: true,
                            showFooter: true,
                            frozenColumns: [[]],
                            onLoadSuccess: function (data) {
                                if(init){
                                    init = false;
                                }else if(data.datas){
                                    $("#popdg").datagrid("loadData", data.datas);
                                }
                                $("#popdg").datagrid("clearSelections");
                            },
                            onClickRow: function (rowIndex, rowData) {
                                rowid = rowData.id;
                                gridname = 'popdg';
                            },
                            onBeforeLoad:function(params){
                                if(init){
                                    return false;
                                }
                            },
                            onDblClickRow:function(index,row){
                                popdgSelOk();
                            }
                        });
            }
            createDataGrid();
            //提示信息
            function showMsg(msg) {
                $.messager.show({
                    title: '提示',
                    msg: msg,
                    showType: 'show'
                });
            }

            function getDataGridSelectedRow() {
                var row = $('#dg').datagrid('getSelected');
                return row;
            }

            //获取表格选中行的指定列(单选)
            function getDataGridSelectedData(field) {
                var row = $('#dg').datagrid('getSelected');
                if (row != null) {
                    value = row[field];
                } else {
                    value = '';
                }
                return value;
            }

            //获取表格选中行的指定列(多选)
            function getDataGridSelectedDatas(field) {
                var ids = '';
                var rows = $('#dg').datagrid('getSelections');
                for (var i = 0; i < rows.length; i++) {
                    ids += rows[i][field];
                    ids += ',';
                }
                ids = ids.substring(0, ids.length - 1);
                return ids;
            }

            function action(fm, op) {
                $.messager.progress({
                    title: '请稍后',
                    msg: '操作进行中...'
                });
                fm.form('submit', {
                    url: "/project/${tableConfig.tableAliasName}",
                    onSubmit: function () {
                        var isValid = $(this).form('validate');
                        if (!isValid) {
                            $.messager.progress('close');	// hide progress bar while the form is invalid
                        }
                        return isValid;	// return false will stop the form submission
                    },
                    success: function (e) {
                        var json = $.parseJSON(e);
                        if (json.errcode != 0) {
                            $.messager.alert("错误", json.errmsg, "error");
                        } else {
                            showMsg("操作成功");
                            if (op == "add") {
                                $('#addDialog').dialog('close');
                            } else if (op == "edit") {
                                $('#editDialog').dialog('close')
                            }
                            refresh();
                        }
                        $.messager.progress('close');	// hide progress bar while submit successfully
                    }
                });
            }

            //添加
            function add() {
                $('#addForm').form('reset');
                $('#addDialog').dialog('open').dialog('setTitle', '添加');
            }

            $("#add").click(function () {
                add();
            });
            //编辑
            function edit() {
                var row = getDataGridSelectedRow();
                if (!row || row == undefined || row == null) {
                    $.messager.alert("注意", "请选择一条数据!", "info");
                    return;
                }
                $('#editForm').form('reset');
                var json = {};
                for (var k in row) {
                    $(json).attr("${tableConfig.tableAliasName}." + k, row[k]);
                }
                $(json).attr("op", "edit");

                $('#editForm').form('load', json);
                $('#editDialog').dialog('open').dialog('setTitle', '编辑');
            }

            //编辑
            $("#edit").click(function () {
                edit();
            });
            //查看
            function view(field) {
                var row = getDataGridSelectedRow();
                if (!row || row == undefined || row == null) {
                    $.messager.alert("注意", "请选择一条数据!", "info");
                    return;
                }
                for (var k in row) {
                    var str = row[k] ? row[k] : "";
                    if (k == "") {

                    }
                <#if tableConfig.dicts?exists>
                    <#list  tableConfig.dicts?keys as key>
                    else if (k == "${acy[key]}") {
                        str = render_${acy[key]}(str);
                    }
                    </#list>
                </#if>
                    $("#view_" + k).text(str);
                }
                $('#viewDialog').dialog('open').dialog('setTitle', '查看');
            <#if tableConfig.subViewObjectId gt 0>
                var subdgdiv = $("#subdgdiv");
                var subtable = $("#subtable");
                var viewdlg = $('#viewDialog');
                var h = viewdlg.height() - subtable.height() - 75;
                if (h < 500) {
                    h = 500;
                }
                subdgdiv.height(h);
                var fieldValue = row[field];
                createSubDataGrid(fieldValue);
            </#if>
            }

            //查看
            $("#view").click(function () {
                view();

            });
            //保存
            $("#addSaveButton").click(function () {
                action($("#addForm"), "add");
            });
            //保存
            $("#editSaveButton").click(function () {
                action($("#editForm"), "edit");
            });
            //查询
            $("#searchButton").click(function () {
                $("#dg").datagrid("load", $("#searchForm").serializeJson());
            });
            //刷新
            function refresh() {
                $("#dg").datagrid("load", $("#searchForm").serializeJson());
            }
            //弹出选择
            function popGrid(code,callback){
                $('#popGrid').css("display","none");
                var json = {};
                $(json).attr("popGridCode", code);
                $('#popGridSearchForm').form('load', json);
                $('#popGrid').dialog('open').dialog('setTitle', '查看');
                var url = "/systemManager/${tableConfig.tableAliasName}?op=popGrid&popGridCode="+code;
                $.get(url,
                        function (data) {
                            createPopDataGrid(code,callback);
                            $("#popdg").datagrid({
                                columns: [data.columns]
                            });
                            $("#popdg").datagrid("loadData", data.datas);
                            $('#popGrid').css("display","");
                        }, 'json');
            }
            //弹出选择_查询
            $("#popSearchButton").click(function () {
                $("#popdg").datagrid("load", $("#popGridSearchForm").serializeJson());
            });
            //弹出选择_重置
            $("#popResetButton").click(function () {
                $("#popGridSearchForm").form('reset');
                $("#popdg").datagrid("load", $("#popGridSearchForm").serializeJson());
            });
            //弹出选择_刷新
            $("#popRefreshButton").click(function () {
                $("#popdg").datagrid("load", $("#popGridSearchForm").serializeJson());
            });
            //弹出选择_确认选择
            function popdgSelOk(){
                var cb = $("#popdg").datagrid('options')["callback"];
                var row = $('#popdg').datagrid('getSelected');
                if (row != null) {
                    cb(row);
                    $('#popGrid').dialog('close');
                } else {
                    $.messager.alert("注意", "请选择一条数据!", "info");
                }
            }
            //弹出选择_选择
            $("#popSelItButton").click(function(){
                popdgSelOk();
            });
            //刷新
            $("#refresh").click(function () {
                refresh();
                popGrid("findtest",function(row){
                        alert(row["address"]);
                })
            });

            //重置
            $("#resetButton").click(function () {
                $('#searchForm').form('reset');
                refresh();
            });
            del = function (id) {
                //获取选中的ID串
                var ids;
                if (id) {
                    ids = id;
                } else {
                    ids = getDataGridSelectedDatas('id');
                }
                if (ids.length <= 0) {
                    $.messager.alert("注意", "请选择至少一条数据", "info");
                    return;
                }
                $.messager.confirm('注意', '确定删除吗?', function (r) {
                    if (r) {
                        $.messager.progress({
                            title: '请稍后',
                            msg: '操作进行中...'
                        });
                        $.ajax({
                            url: "/project/${tableConfig.tableAliasName}",
                            data: {'ids': ids, 'op': 'delete'},
                            type: "Post",
                            dataType: "json",
                            success: function (data) {
                                $.messager.progress('close');
                                showMsg("删除成功");
                                refresh();
                                if (data.errcode != 0) {
                                    $.messager.alert("错误", data.errmsg, "error");
                                }
                            },
                            error: function (data) {
                                $.messager.progress('close');
                                $.messager.alert('错误', data, "error");
                            }
                        });
                    }
                });
            }
            //删除
            $("#delete").click(function () {
                del();
            });
<#if tableConfig.subViewObjectId gt 0>
            ///////////////////////////////////////////////子表操作////////////////////////////////////////
            function getSubDataGridSelectedRow() {
                var row = $('#subdg').datagrid('getSelected');
                return row;
            }

            //获取表格选中行的指定列(单选)
            function getSubDataGridSelectedData(field) {
                var row = $('#subdg').datagrid('getSelected');
                if (row != null) {
                    value = row[field];
                } else {
                    value = '';
                }
                return value;
            }

            //获取表格选中行的指定列(多选)
            function getSubDataGridSelectedDatas(field) {
                var ids = '';
                var rows = $('#subdg').datagrid('getSelections');
                for (var i = 0; i < rows.length; i++) {
                    ids += rows[i][field];
                    ids += ',';
                }
                ids = ids.substring(0, ids.length - 1);
                return ids;
            }

            function subAction(fm, op) {
                $.messager.progress({
                    title: '请稍后',
                    msg: '操作进行中...'
                });
                fm.form('submit', {
                    url: "/systemManager/userManager",
                    onSubmit: function () {
                        var isValid = $(this).form('validate');
                        if (!isValid) {
                            $.messager.progress('close');	// hide progress bar while the form is invalid
                        }
                        return isValid;	// return false will stop the form submission
                    },
                    success: function (e) {
                        var json = $.parseJSON(e);
                        if (json.errcode != 0) {
                            $.messager.alert("错误", json.errmsg, "error");
                        } else {
                            showMsg("操作成功");
                            if (op == "add") {
                                $('#subAddDialog').dialog('close');
                            } else if (op == "edit") {
                                $('#subEditDialog').dialog('close')
                            }
                            subRefresh();
                        }
                        $.messager.progress('close');	// hide progress bar while submit successfully
                    }
                });
            }

            //添加
            function subAdd() {
                $('#subAddForm').form('reset');
                $('#subAddDialog').dialog('open').dialog('setTitle', '添加');
            }

            $("#subAdd").click(function () {
                subAdd();
            });
            //编辑
            function subEdit() {
                var row = getSubDataGridSelectedRow();
                if (!row || row == undefined || row == null) {
                    $.messager.alert("注意", "请选择一条数据!", "info");
                    return;
                }
                $('#subEditForm').form('reset');
                var json = {};
                for (var k in row) {
                    $(json).attr("user." + k, row[k]);
                }
                $(json).attr("op", "edit");

                $('#subEditForm').form('load', json);
                $('#subEditDialog').dialog('open').dialog('setTitle', '编辑');
            }

            //编辑
            $("#subEdit").click(function () {
                subEdit();
            });
            //查看
            function subView() {
                var row = getSubDataGridSelectedRow();
                if (!row || row == undefined || row == null) {
                    $.messager.alert("注意", "请选择一条数据!", "info");
                    return;
                }
                for (var k in row) {
                    var str = row[k] ? row[k] : "";
                    if (k == "") {

                    } else if (k == "sex") {
                        str = renderSex(str);
                    } else if (k == "status") {
                        str = renderStatus(str);
                    }
                    $("#view_sub_" + k).text(str);
                }
                $('#subViewDialog').dialog('open').dialog('setTitle', '查看');
            }

            //查看
            $("#subView").click(function () {
                subView();

            });
            //保存
            $("#subSddSaveButton").click(function () {
                action($("#subAddForm"), "add");
            });
            //保存
            $("#subEditSaveButton").click(function () {
                action($("#subEditForm"), "edit");
            });
            //查询
            $("#subSearchButton").click(function () {
                $("#subdg").datagrid("load", $("#subSearchForm").serializeJson());
            });
            //刷新
            function subRefresh() {
                $("#subdg").datagrid("load", $("#subSearchForm").serializeJson());
            }

            //刷新
            $("#subRefresh").click(function () {
                subRefresh();
            });
            //重置
            $("#subResetButton").click(function () {
                $('#subSearchForm').form('reset');
                subRefresh();
            });
            subDel = function (id) {
                //获取选中的ID串
                var ids;
                if (id) {
                    ids = id;
                } else {
                    ids = getSubDataGridSelectedDatas('id');
                }
                if (ids.length <= 0) {
                    $.messager.alert("注意", "请选择至少一条数据", "info");
                    return;
                }
                $.messager.confirm('注意', '确定删除吗?', function (r) {
                    if (r) {
                        $.messager.progress({
                            title: '请稍后',
                            msg: '操作进行中...'
                        });
                        $.ajax({
                            url: "/systemManager/userManager",
                            data: {'ids': ids, 'op': 'delete'},
                            type: "Post",
                            dataType: "json",
                            success: function (data) {
                                $.messager.progress('close');
                                showMsg("删除成功");
                                refresh();
                                if (data.errcode != 0) {
                                    $.messager.alert("错误", data.errmsg, "error");
                                }
                            },
                            error: function (data) {
                                $.messager.progress('close');
                                $.messager.alert('错误', data, "error");
                            }
                        });
                    }
                });
            }
            //删除
            $("#subDelete").click(function () {
                subDel();
            });
</#if>
            init = false;
            setTimeout(function(){
                $.parser.parse();
            },1000);
        });
    </script>
</head>
<body>
<div id='Loading' style="position:absolute;z-index:1000;top:0px;left:0px;width:100%;height:100%;background:#DDDDDB;text-align:center;padding-top: 20%;"><h1><img src="/js/easyui/themes/gray/images/loading.gif"><font color="#15428B">加载中···</font></h1></div>
<table width="100%" id="dg" toolbar="#dg_tb"></table>
<div id="dg_tb" style="padding:3px; height: auto">
    <div style="height:30px;" class="datagrid-toolbar">
	<span style="float:left;">

	<a id="add" href="javascript:" class="easyui-linkbutton" plain="true" icon="add">添加</a>
	<a id="edit" href="javascript:" class="easyui-linkbutton" plain="true" icon="icon-edit">编辑</a>
	<a id="delete" href="javascript:" class="easyui-linkbutton" plain="true" icon="delete">删除</a>
	<a id="view" href="javascript:" class="easyui-linkbutton" plain="true" icon="icon-search">查看</a>
        <a id="refresh" href="javascript:" class="easyui-linkbutton" plain="true" icon="arrowrefresh">刷新</a>

	</span>
    </div>
    <div name="searchPanel">

        <form id="searchForm" action="/systemManager/userManager" method="post">
            <input type="hidden" name="op" value="view"/>
            <table style="font-size: 12px;text-align: right">
<#list tableConfig.allQueryPojos as pojo>
    <#if pojo.maxLength gt 0 && pojo.validateType??>
        <#assign validate=",validType:['${pojo.validateType}','length[0,${pojo.maxLength}]']">
    <#elseif pojo.maxLength gt 0>
        <#assign validate=",validType:{length:[0,${pojo.maxLength}]}">
    <#elseif pojo.validateType??>
        <#assign validate=",validType:'${pojo.validateType}'">
    <#else>
        <#assign validate="">
    </#if>
    <#assign tdCount=tdCount+1>
            <#if pojo_index%3==0&&pojo_has_next>
                <tr>
            </#if>
                    <td>${pojo.displayName}:</td>
                    <td class="tdRightPadding">
                    <#if tdCount==3&&!pojo_has_next>
                        <#assign inputStyle="style='width:110px'">
                    <#else>
                        <#assign inputStyle="">
                    </#if>
                    <#if pojo.dictCode??>
                        <input id="search_${pojo.columnName}" ${inputStyle}  class="easyui-combobox" name="${tableConfig.tableAliasName}.${pojo.columnName}"
                               data-options="
                     onShowPanel:function(){showCombboxData('search_${pojo.columnName}','${pojo.dictCode}')},
                    method:'get',
                    required:false,
                    valueField:'value',
                    textField:'label',
                    editable:false,
                    panelHeight:'auto'${validate}">
                    <#else>
                        <input  ${inputStyle}  name="${tableConfig.tableAliasName}.${pojo.columnName}" data-options="required:false${validate}" class="f1 easyui-textbox"/>
                    </#if>
                    <#if tdCount==3&&!pojo_has_next>
                        <a href="javascript:" id="searchButton" class="easyui-linkbutton" icon="find"></a><a
                            href="javascript:" id="resetButton" class="easyui-linkbutton" icon="erase"></a>
                        </td>
                    <#elseif !pojo_has_next>
                        </td>
                        <td></td>
                        <td><a href="javascript:" id="searchButton" class="easyui-linkbutton" icon="find">查询</a><a
                                href="javascript:" id="resetButton" class="easyui-linkbutton" icon="erase">重置</a></td>
                    <#else>
                        </td>
                    </#if>
            <#if tdCount==3||!pojo_has_next>
                <#assign tdCount=0>
                </tr>
            </#if>
</#list>
            </table>
        </form>
    </div>
</div>
    <div id="addDialog" class="easyui-dialog" style="width: 800px; height: auto; padding: 10px 20px"
         data-options="closed:true,buttons:'#addDialog-buttons',modal:true,iconCls:'icon-add'">
        <form id="addForm" action="/systemManager/userManager" method="post">
            <input type="hidden" name="op" value="add"/>
            <table style="font-size: 12px;text-align: right">
            <#list tableConfig.allAddPojos as pojo>
                <#if pojo.maxLength gt 0 && pojo.validateType??>
                    <#assign validate=",validType:['${pojo.validateType}','length[0,${pojo.maxLength}]']">
                <#elseif pojo.maxLength gt 0>
                    <#assign validate=",validType:{length:[0,${pojo.maxLength}]}">
                <#elseif pojo.validateType??>
                    <#assign validate=",validType:'${pojo.validateType}'">
                <#else>
                    <#assign validate="">
                </#if>
                <#assign tdCount=tdCount+1>
                <#if pojo_index%3==0&&pojo_has_next>
                <tr>
                </#if>
                <td>${pojo.displayName}:</td>
            <td class="tdRightPadding">
                <#if tdCount==3&&!pojo_has_next>
                    <#assign inputStyle="style='width:110px'">
                <#else>
                    <#assign inputStyle="">
                </#if>
                <#if pojo.dictCode??>
                    <input id="add_${pojo.columnName}" ${inputStyle}  class="easyui-combobox" name="${tableConfig.tableAliasName}.${pojo.columnName}"
                           data-options="
                     onShowPanel:function(){showCombboxData('add_${pojo.columnName}','${pojo.dictCode}')},
                    method:'get',
                    required:${pojo.required?c},
                    valueField:'value',
                    textField:'label',
                    editable:false,
                    panelHeight:'auto'${validate}">
                <#else>
                    <input  ${inputStyle}  name="${tableConfig.tableAliasName}.${pojo.columnName}" data-options="required:${pojo.required?c}${validate}" class="f1 easyui-textbox"/>
                </#if>
                </td>
                <#if tdCount==3||!pojo_has_next>
                    <#assign tdCount=0>
                </tr>
                </#if>
            </#list>

            </table>
        </form>
        <div id="addDialog-buttons">
            <a href="javascript:void(0)" class="easyui-linkbutton" id="addSaveButton"
               data-options="iconCls:'icon-save',plain:true">保存</a>
            <a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-cancel',plain:true"
               onclick="javascript:$('#addDialog').dialog('close')">取消</a>
        </div>
    </div>
    <div id="editDialog" class="easyui-dialog" style="width: 800px; height: auto; padding: 10px 20px"
         data-options="closed:true,buttons:'#editDialog-buttons',modal:true,iconCls:'icon-edit'">
        <form id="editForm" action="/systemManager/userManager" method="post">
            <input type="hidden" name="op" value="edit"/>
            <input type="hidden" name="${tableConfig.tableAliasName}.id" value="0"/>
            <table style="font-size: 12px;text-align: right">
            <#list tableConfig.allEditPojos as pojo>
                <#if pojo.maxLength gt 0 && pojo.validateType??>
                    <#assign validate=",validType:['${pojo.validateType}','length[0,${pojo.maxLength}]']">
                <#elseif pojo.maxLength gt 0>
                    <#assign validate=",validType:{length:[0,${pojo.maxLength}]}">
                <#elseif pojo.validateType??>
                    <#assign validate=",validType:'${pojo.validateType}'">
                <#else>
                    <#assign validate="">
                </#if>
                <#assign tdCount=tdCount+1>
                <#if pojo_index%3==0&&pojo_has_next>
                <tr>
                </#if>
                <td>${pojo.displayName}:</td>
                <td class="tdRightPadding">

                    <#if tdCount==3&&!pojo_has_next>
                        <#assign inputStyle="style='width:110px'">
                    <#else>
                        <#assign inputStyle="">
                    </#if>
                    <#if pojo.dictCode??>
                        <input id="edit_${pojo.columnName}" ${inputStyle}  class="easyui-combobox" name="${tableConfig.tableAliasName}.${pojo.columnName}"
                               data-options="
                     onShowPanel:function(){showCombboxData('edit_${pojo.columnName}','${pojo.dictCode}')},
                    method:'get',
                    required:${pojo.required?c},
                    valueField:'value',
                    textField:'label',
                    editable:false,
                    panelHeight:'auto'${validate}">
                    <#else>
                        <input  ${inputStyle}  name="${tableConfig.tableAliasName}.${pojo.columnName}" data-options="required:${pojo.required?c}${validate}" class="f1 easyui-textbox"/>
                    </#if>
                </td>
                <#if tdCount==3||!pojo_has_next>
                    <#assign tdCount=0>
                </tr>
                </#if>
            </#list>
            </table>
        </form>
        <div id="editDialog-buttons">
            <a href="javascript:void(0)" class="easyui-linkbutton" id="editSaveButton"
               data-options="iconCls:'icon-save',plain:true">保存</a>
            <a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-cancel',plain:true"
               onclick="javascript:$('#editDialog').dialog('close')">取消</a>
        </div>
    </div>
    <div id="viewDialog" class="easyui-dialog" style="width: 800px; ${subTableDialogHeight} padding: 10px 20px"
         data-options="closed:true,buttons:'#viewDialog-buttons',modal:true,iconCls:'icon-search'">
        <table style="font-size: 12px;text-align: right" id="subable">
        <#list tableConfig.allEditPojos as pojo>
            <#assign tdCount=tdCount+1>
            <#if pojo_index%3==0&&pojo_has_next>
            <tr>
            </#if>
            <td>${pojo.displayName}:</td>
            <td class="tdRightPadding" id="view_${pojo.columnName}"></td>
            <#if tdCount==3||!pojo_has_next>
                <#assign tdCount=0>
            </tr>
            </#if>
        </#list>
        </table>
<#if tableConfig.subViewObjectId gt 0>
        <div id="subdgdiv">
            <div id="subdg" toolbar="#subdg_tb"/>
            <div id="subdg_tb" style="padding:3px; height: auto">
                <div style="height:30px;" class="datagrid-toolbar">
	<span style="float:left;">

	<a id="subAdd" href="javascript:" class="easyui-linkbutton" plain="true" icon="add">添加</a>
	<a id="subEdit" href="javascript:" class="easyui-linkbutton" plain="true" icon="icon-edit">编辑</a>
	<a id="subDelete" href="javascript:" class="easyui-linkbutton" plain="true" icon="delete">删除</a>STATUS
	<a id="subView" href="javascript:" class="easyui-linkbutton" plain="true" icon="icon-search">查看</a>
        <a id="subRefresh" href="javascript:" class="easyui-linkbutton" plain="true" icon="arrowrefresh">刷新</a>

	</span>
                </div>
                <div name="searchPanel">

                    <form id="subSearchForm" action="/systemManager/userManager" method="post">
                        <input type="hidden" name="op" value="view"/>
                        <table style="font-size: 12px;text-align: right">
                            <tr>
                                <td>店铺名称:</td>
                                <td class="tdRightPadding"><input name="addasShop.shopName"
                                                                  data-options="validType:{length:[3,15]}"
                                                                  class="f1 easyui-textbox"/></td>
                                <td>地址:</td>
                                <td class="tdRightPadding"><input name="addasShop.address" class="f1 easyui-textbox"
                                                                  data-options="validType:['length[6,18]']"/>
                                </td>
                                <td>状态:</td>
                                <td class="tdRightPadding"><input id="subSearch_status" style="width: 110px;" class="easyui-combobox"
                                                                  name="user.status"
                                                                  data-options="
                    onShowPanel:function(){showCombboxData('subSearch_status','acyframework_users_status')},
                    method:'get',
                    valueField:'value',
                    textField:'label',
                    editable:false,
                    panelHeight:'auto'">

                                    <a href="javascript:" title="查询" id="subSearchButton" class="easyui-linkbutton"
                                       icon="find"></a><a
                                            href="javascript:" title="重置" id="subResetButton" class="easyui-linkbutton"
                                            icon="erase"></a>

                                </td>
                            </tr>
                        </table>
                    </form>
                </div>
            </div>
        </div>
</#if>
        <div id="viewDialog-buttons">
            <a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-cancel',plain:true"
               onclick="javascript:$('#viewDialog').dialog('close')">取消</a>
        </div>
    </div>
<#if tableConfig.subViewObjectId gt 0>
<div id="subdlgs">
    <div id="subAddDialog" class="easyui-dialog" style="width: 800px; height: auto; padding: 10px 20px"
         data-options="closed:true,buttons:'#subAddDialog-buttons',modal:true,iconCls:'icon-add'">
        <form id="subAddForm" action="/systemManager/userManager" method="post">
            <input type="hidden" name="op" value="add"/>
            <table style="font-size: 12px;text-align: right">
                <tr>
                    <td>登录ID:</td>
                    <td class="tdRightPadding"><input name="user.username"
                                                      data-options="required:true,validType:{length:[3,15]}"
                                                      class="f1 easyui-textbox"/></td>
                    <td>密码:</td>
                    <td class="tdRightPadding"><input name="user.password" class="f1 easyui-textbox"
                                                      data-options="required:true,validType:['length[6,18]']"/>
                    </td>
                    <td>姓名:</td>
                    <td class="tdRightPadding"><input name="user.userRealName" class="f1 easyui-textbox"
                                                      data-options="required:true,validType:{length:[2,4]}"/></td>
                </tr>
                <tr>
                    <td>性别:</td>
                    <td class="tdRightPadding"><input id="subAdd_sex" required="true" class="easyui-combobox" name="user.sex"
                                                      data-options="
                    onShowPanel:function(){showCombboxData('subAdd_sex','acyframework_users_sex')},
                    method:'get',
                    valueField:'value',
                    textField:'label',
                    editable:false,
                    panelHeight:'auto'">
                    </td>
                    <td>状态:</td>
                    <td class="tdRightPadding"><input id="subAdd_status" required="true" class="easyui-combobox" name="user.status"
                                                      data-options="
                    onShowPanel:function(){showCombboxData('subAdd_status','acyframework_users_status')},
                    method:'get',
                    valueField:'value',
                    textField:'label',
                    editable:false,
                    panelHeight:'auto'">
                    </td>
                    <td>手机:</td>
                    <td class="tdRightPadding"><input name="user.mobile" class="f1 easyui-textbox"/></td>
                </tr>
                <tr>
                    <td>固定电话:</td>
                    <td class="tdRightPadding"><input name="user.phone" class="f1 easyui-textbox"/></td>
                    <td>地址:</td>
                    <td class="tdRightPadding"><input name="user.address" class="f1 easyui-textbox"
                                                      data-options="required:false,validType:['length[1,255]']"/>
                    </td>
                    <td>QQ号码:</td>
                    <td class="tdRightPadding"><input name="user.QQ" class="f1 easyui-textbox"/></td>
                </tr>
                <tr>
                    <td>邮箱地址:</td>
                    <td class="tdRightPadding"><input name="user.mail" class="f1 easyui-textbox"
                                                      data-options="validType:['email']"/></td>
                </tr>

            </table>
        </form>
        <div id="subAddDialog-buttons">
            <a href="javascript:void(0)" class="easyui-linkbutton" id="subAddSaveButton"
               data-options="iconCls:'icon-save',plain:true">保存</a>
            <a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-cancel',plain:true"
               onclick="javascript:$('#subAddDialog').dialog('close')">取消</a>
        </div>
    </div>
    <div id="subEditDialog" class="easyui-dialog" style="width: 800px; height: auto; padding: 10px 20px"
         data-options="closed:true,buttons:'#subEditDialog-buttons',modal:true,iconCls:'icon-edit'">
        <form id="subEditForm" action="/systemManager/userManager" method="post">
            <input type="hidden" name="op" value="edit"/>
            <input type="hidden" name="user.id" value="0"/>
            <table style="font-size: 12px;text-align: right">
                <tr>
                    <td>登录ID:</td>
                    <td class="tdRightPadding"><input name="user.username"
                                                      data-options="required:true,validType:{length:[3,15]}"
                                                      class="f1 easyui-textbox"/></td>
                    <td>密码:</td>
                    <td class="tdRightPadding"><input name="user.password" class="f1 easyui-textbox"
                                                      data-options="required:true,validType:['length[6,18]']"/>
                    </td>
                    <td>姓名:</td>
                    <td class="tdRightPadding"><input name="user.userRealName" class="f1 easyui-textbox"
                                                      data-options="required:true,validType:{length:[2,4]}"/></td>
                </tr>
                <tr>
                    <td>性别:</td>
                    <td class="tdRightPadding"><input id="subEdit_sex" required="true" class="easyui-combobox" name="user.sex"
                                                      data-options="
                    onShowPanel:function(){showCombboxData('subEdit_sex','acyframework_users_sex')},
                    method:'get',
                    valueField:'value',
                    textField:'label',
                    editable:false,
                    panelHeight:'auto'">
                    </td>
                    <td>状态:</td>
                    <td class="tdRightPadding"><input required="true" id="subEdit_status" class="easyui-combobox" name="user.status"
                                                      data-options="
                    onShowPanel:function(){showCombboxData('subEdit_status','acyframework_users_status')},
                    method:'get',
                    valueField:'value',
                    textField:'label',
                    editable:false,
                    panelHeight:'auto'">
                    </td>
                    <td>手机:</td>
                    <td class="tdRightPadding"><input name="user.mobile" class="f1 easyui-textbox"/></td>
                </tr>
                <tr>
                    <td>固定电话:</td>
                    <td class="tdRightPadding"><input name="user.phone" class="f1 easyui-textbox"/></td>
                    <td>地址:</td>
                    <td class="tdRightPadding"><input name="user.address" class="f1 easyui-textbox"
                                                      data-options="required:false,validType:['integer','length[1,5]']"/>
                    </td>
                    <td>QQ号码:</td>
                    <td class="tdRightPadding"><input name="user.QQ" class="f1 easyui-textbox"/></td>
                </tr>
                <tr>
                    <td>邮箱地址:</td>
                    <td class="tdRightPadding"><input name="user.mail" class="f1 easyui-textbox"
                                                      data-options="validType:['email']"/></td>
                </tr>

            </table>
        </form>
        <div id="subEditDialog-buttons">
            <a href="javascript:void(0)" class="easyui-linkbutton" id="subEditSaveButton"
               data-options="iconCls:'icon-save',plain:true">保存</a>
            <a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-cancel',plain:true"
               onclick="javascript:$('#subEditDialog').dialog('close')">取消</a>
        </div>
    </div>
    <div id="subViewDialog" class="easyui-dialog" style="width: 800px; padding: 10px 20px"
         data-options="closed:true,buttons:'#subViewDialog-buttons',modal:true,iconCls:'icon-search'">
        <table style="font-size: 12px;text-align: right">
            <tr>
                <td style="width: 60px;">店铺名称:</td>
                <td class="tdRightPadding" id="view_sub_shopName"></td>
                <td style="width: 60px;">店铺地址:</td>
                <td class="tdRightPadding" id="view_sub_address"></td>
                <td style="width: 60px;">营业时间:</td>
                <td class="tdRightPadding" id="view_sub_openDate"></td>
            </tr>
            <tr>
                <td style="width: 60px;">状态:</td>
                <td class="tdRightPadding" id="view_sub_status"></td>
                <td style="width: 60px;">店龄:</td>
                <td class="tdRightPadding" id="view_sub_age"></td>
                <td></td>
                <td></td>
            </tr>

        </table>


        <div id="subViewDialog-buttons">
            <a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-cancel',plain:true"
               onclick="javascript:$('#subViewDialog').dialog('close')">取消</a>
        </div>
    </div>
    <div id="popGrid" class="easyui-dialog" style="width: 800px; height: 400px; padding: 10px 20px"
         data-options="closed:true,buttons:'#popGridDialog-buttons',modal:true,iconCls:'icon-search'">

        <div id="popdg" toolbar="#popdg_tb"/>
        <div id="popdg_tb" style="padding:3px; height: auto">
            <div name="searchPanel">

                <form id="popGridSearchForm" action="" method="post">
                    <input type="hidden" name="op" value="popGrid"/>
                    <input type="hidden" name="popGridCode" value=""/>
                    <input name="searchStr"
                           data-options="validType:{length:[1,255]}"
                           class="f1 easyui-textbox"/>
                    <a href="javascript:" id="popSearchButton" class="easyui-linkbutton" icon="find">查询</a>
                    <a href="javascript:" id="popResetButton" class="easyui-linkbutton" icon="erase">重置</a>
                    <a id="popRefreshButton" href="javascript:" class="easyui-linkbutton"  icon="arrowrefresh">刷新</a>
                </form>
            </div>
        </div>

        <div id="popGridDialog-buttons">
            <a href="javascript:void(0)" class="easyui-linkbutton" id="popSelItButton" data-options="iconCls:'accept',plain:true">选择</a>
            <a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-cancel',plain:true"
               onclick="javascript:$('#popGrid').dialog('close')">取消</a>
        </div>
    </div>
</div>
</#if>
</body>
</html>