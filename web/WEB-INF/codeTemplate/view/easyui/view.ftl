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
        var pop;
        var subPop
        function showCombboxData(itemId, code) {
            var data = $("#" + itemId).combobox("getData");
            if (data == null || data == "") {
                $("#" + itemId).combobox("reload", "/getDict?code=" + code);
            }
        }
        $.parser.onComplete = function (context) {
            $("#Loading").css("display", "none");
        };
        $(document).ready(function () {
        <#if tableConfig.hasSelectInput>
            pop = function (sqlCode, columnName, op) {
                popGrid(sqlCode, popCallBack, columnName, op);
            };
            function popCallBack(row) {
                var col = $("#popdg").datagrid('options')["columnName"];
                var op = $("#popdg").datagrid('options')["op"];
                var json;
                if (op == "add") {
                    json = $("#addForm").serializeJson();
                    <#list tableConfig.allAddPojos as pojo>
                        <#if pojo.inputType??&&pojo.inputType=="selectInput">
                            if (col == '${pojo.columnName}') {
                                <#list   pojo.selectInputConfig?keys as mk>
                                    json['${tableConfig.tableAliasName}.${mk}'] = row["${pojo.selectInputConfig[mk]}"];
                                </#list>
                            }
                        </#if>
                    </#list>
                    $("#addForm").form("load", json);
                } else {
                    json = $("#editForm").serializeJson();
                    <#list tableConfig.allAddPojos as pojo>
                        <#if pojo.inputType??&&pojo.inputType=="selectInput">
                            if (col == '${pojo.columnName}') {
                                <#list   pojo.selectInputConfig?keys as mk>
                                    json['${tableConfig.tableAliasName}.${mk}'] = row["${pojo.selectInputConfig[mk]}"];
                                </#list>
                            }
                        </#if>
                    </#list>
                    $("#editForm").form("load", json);
                }
            }
        </#if>

        <#if tableConfig.subTableConfig??&&tableConfig.subTableConfig.hasSelectInput>
            subPop = function (sqlCode, columnName, op) {
                popGrid(sqlCode, subPopCallBack, columnName, op);
            };
            function subPopCallBack(row) {
                var col = $("#popdg").datagrid('options')["columnName"];
                var op = $("#popdg").datagrid('options')["op"];
                var json;
                if (op == "subAdd") {
                    json = $("#subAddForm").serializeJson();
                    <#list tableConfig.subTableConfig.allAddPojos as pojo>
                        <#if pojo.inputType??&&pojo.inputType=="selectInput">
                            if (col == '${pojo.columnName}') {
                                <#list   pojo.selectInputConfig?keys as mk>
                                    json['${tableConfig.subTableConfig.tableAliasName}.${mk}'] = row["${pojo.selectInputConfig[mk]}"];
                                </#list>
                            }
                        </#if>
                    </#list>
                    $("#subAddForm").form("load", json);
                } else {
                    json = $("#subEditForm").serializeJson();
                    <#list tableConfig.subTableConfig.allAddPojos as pojo>
                        <#if pojo.inputType??&&pojo.inputType=="selectInput">
                            if (col == '${pojo.columnName}') {
                                <#list   pojo.selectInputConfig?keys as mk>
                                    json['${tableConfig.subTableConfig.tableAliasName}.${mk}'] = row["${pojo.selectInputConfig[mk]}"];
                                </#list>
                            }
                        </#if>
                    </#list>
                    $("#subEditForm").form("load", json);
                }
            }
        </#if>
            /////////////////////////字典匹配////////////////////////////////////////////////////
        <#if tableConfig.dicts?exists>
            <#list  tableConfig.dicts?keys as key>
                function render_${acy[key]}(value) {
                    <#list  tableConfig.dicts[key] as obj>
                        if (value == '${obj.value}') {
                            return "${obj.label}";
                        }
                    </#list>
                }

            </#list>
        </#if>
<#if tableConfig.subViewObjectId gt 0>
        <#if tableConfig.subTableConfig.dicts?exists>
            <#list  tableConfig.subTableConfig.dicts?keys as key>
                function render_sub_${subAcy[key]}(value) {
                    <#list  tableConfig.subTableConfig.dicts[key] as obj>
                        if (value == '${obj.value}') {
                            return "${obj.label}";
                        }
                    </#list>
                }

            </#list>
        </#if>
</#if>
            function createDataGrid() {
                var initUrl = '/project/${tableConfig.tableAliasName}?op=view';
                initUrl = encodeURI(initUrl);
                $('#dg').datagrid(
                        {

                            url: initUrl,
                            idField: '${tableConfig.primaryKey}',
                            title: '${tableConfig.title}',
                            fit: true,
                            fitColumns: true,
                            pageSize: 30,
                            pagination: true,
                            singleSelect: false,
                            selectOnCheck: true,
                            sortName: '${tableConfig.primaryKey}',
                            pageList: [10, 30, 50, 100, 300, 500, 800, 1000],
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
                            onDblClickRow: function (index, row) {
                            <#if tableConfig.subViewObjectId gt 0>
                                view("${tableConfig.selfColumnForSubViewLink}");
                            <#else>
                                view();
                            </#if>
                            }
                        });
            }
        <#if tableConfig.subViewObjectId gt 0>
            function createSubDataGrid(pvalue) {
                var initUrl = '/project/${tableConfig.tableAliasName}/subTableManager?op=view&${tableConfig.subTableConfig.tableAliasName}.${tableConfig.subViewLinkColumn}=' + pvalue;
                initUrl = encodeURI(initUrl);
                $('#subdg').datagrid(
                        {

                            url: initUrl,
                            idField: '${tableConfig.subTableConfig.primaryKey}',
                            title: '${tableConfig.subTableConfig.title}',
                            fit: true,
                            fitColumns: true,
                            pageSize: 30,
                            pagination: true,
                            singleSelect: false,
                            selectOnCheck: true,
                            sortName: '${tableConfig.subTableConfig.primaryKey}',
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
                                    <#list tableConfig.subTableConfig.allDisplayPojos as pojo>
                                        {
                                            field: '${pojo.columnName}',
                                            title: '${pojo.displayName}',
                                            <#if pojo.dictCode??>
                                                formatter: render_sub_${subAcy[pojo.dictCode]},
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
                                        var href = '';
                                        <#if tableConfig.subDelete>
                                        href = "<a href='javascript:' title='删除' onclick='subDel(" + rec.id + ")'><img src='/icons/delete.png'/></a>";
                                        </#if>
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
                            onDblClickRow: function (index, row) {
                                $("#subView").trigger("click");
                            }
                        });
            }
        </#if>
        <#if tableConfig.hasSelectInput||(tableConfig.subTableConfig??&&tableConfig.subTableConfig.hasSelectInput)>
            function createPopDataGrid(code, callback, columnName, op) {
                var url = "/project/${tableConfig.tableAliasName}?op=popGrid&popGridCode=" + code;
                var init = true;
                $('#popdg').datagrid(
                        {
                            url: url,
                            idField: 'id',
                            fit: true,
                            fitColumns: true,
                            pageSize: 10,
                            callback: callback,
                            columnName: columnName,
                            op: op,
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
                                if (init) {
                                    init = false;
                                } else if (data.datas) {
                                    $("#popdg").datagrid("loadData", data.datas);
                                }
                                $("#popdg").datagrid("clearSelections");
                            },
                            onClickRow: function (rowIndex, rowData) {
                                rowid = rowData.id;
                                gridname = 'popdg';
                            },
                            onBeforeLoad: function (params) {
                                if (init) {
                                    return false;
                                }
                            },
                            onDblClickRow: function (index, row) {
                                popdgSelOk();
                            }
                        });
            }
        </#if>
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
            <aqu_add_or_edit>
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
                                $('#editDialog').dialog('close');
                            }
                            refresh();
                        }
                        $.messager.progress('close');	// hide progress bar while submit successfully
                    }
                });
            }
            </aqu_add_or_edit>
            <aqu_add>
            //添加
            function add() {
                $('#addForm').form('reset');
                $('#addDialog').dialog('open').dialog('setTitle', '添加');
            }

            $("#add").click(function () {
                add();
            });
            </aqu_add>
            <aqu_edit>
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
            </aqu_edit>
            //查看
            function view(field) {
                var row = getDataGridSelectedRow();
                if (!row || row == undefined || row == null) {
                    $.messager.alert("注意", "请选择一条数据!", "info");
                    return;
                }
                for (var k in row) {
                    var str = row[k] ? row[k] : "";
                if (k == "")
                {

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
<#if tableConfig.subViewObjectId gt 0>
                view("${tableConfig.selfColumnForSubViewLink}");
<#else>
                view();
</#if>

            });
            <aqu_add>
            //保存
            $("#addSaveButton").click(function () {
                action($("#addForm"), "add");
            });
            </aqu_add>
            <aqu_edit>
            //保存
            $("#editSaveButton").click(function () {
                action($("#editForm"), "edit");
            });
            </aqu_edit>
            //查询
            $("#searchButton").click(function () {
                $("#dg").datagrid("load", $("#searchForm").serializeJson());
            });
            //刷新
            function refresh() {
                $("#dg").datagrid("load", $("#searchForm").serializeJson());
            }
        <#if tableConfig.hasSelectInput||(tableConfig.subTableConfig??&&tableConfig.subTableConfig.hasSelectInput)>
            //弹出选择
            function popGrid(code, callback, columnName, op) {
                $('#popGrid').css("display", "none");
                var json = {};
                $(json).attr("popGridCode", code);
                $('#popGridSearchForm').form('load', json);
                $('#popGrid').dialog('open').dialog('setTitle', '查看');
                var url = "/project/${tableConfig.tableAliasName}?op=popGrid&popGridCode=" + code;
                $.get(url,
                        function (data) {
                            createPopDataGrid(code, callback, columnName, op);
                            $("#popdg").datagrid({
                                columns: [data.columns]
                            });
                            $("#popdg").datagrid("loadData", data.datas);
                            $('#popGrid').css("display", "");
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
            function popdgSelOk() {
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
            $("#popSelItButton").click(function () {
                popdgSelOk();
            });
        </#if>
            //刷新
            $("#refresh").click(function () {
                refresh();
            });

            //重置
            $("#resetButton").click(function () {
                $('#searchForm').form('reset');
                refresh();
            });
            <aqu_delete>
            del = function (id) {
                //获取选中的ID串
                var ids;
                if (id) {
                    ids = id;
                } else {
                    ids = getDataGridSelectedDatas('${tableConfig.primaryKey}');
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
                                $.messager.alert('服务器发生错误', "错误代码:"+data.status, "error");
                            }
                        });
                    }
                });
            }
            </aqu_delete>
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
            <#if tableConfig.subAdd||tableConfig.subEdit>
            function subAction(fm, op) {
                $.messager.progress({
                    title: '请稍后',
                    msg: '操作进行中...'
                });
                fm.form('submit', {
                    url: "/project/${tableConfig.tableAliasName}/subTableManager",
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
            </#if>
            <#if tableConfig.subAdd>
            //添加
            function subAdd() {
                var json={};
                json["${tableConfig.subTableConfig.tableAliasName}.${tableConfig.subViewLinkColumn}"] = getDataGridSelectedData("${tableConfig.selfColumnForSubViewLink}");
                $('#subAddForm').form('reset');
                $('#subAddForm').form('load',json);
                $('#subAddDialog').dialog('open').dialog('setTitle', '添加');
            }

            $("#subAdd").click(function () {
                subAdd();
            });
                //保存
                $("#subAddSaveButton").click(function () {
                    subAction($("#subAddForm"), "add");
                });
            </#if>
            <#if tableConfig.subEdit>
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
                    $(json).attr("${tableConfig.subTableConfig.tableAliasName}." + k, row[k]);
                }
                $(json).attr("op", "edit");

                $('#subEditForm').form('load', json);
                $('#subEditDialog').dialog('open').dialog('setTitle', '编辑');
            }

            //编辑
            $("#subEdit").click(function () {
                subEdit();
            });
                //保存
                $("#subEditSaveButton").click(function () {
                    subAction($("#subEditForm"), "edit");
                });
            </#if>
            //查看
            function subView() {
                var row = getSubDataGridSelectedRow();
                if (!row || row == undefined || row == null) {
                    $.messager.alert("注意", "请选择一条数据!", "info");
                    return;
                }
                for (var k in row) {
                    var str = row[k] ? row[k] : "";
                if (k == "")
                {

                }
                    <#if tableConfig.subTableConfig.dicts?exists>
                        <#list  tableConfig.subTableConfig.dicts?keys as key>
                        else if (k == "${subAcy[key]}") {
                            str = render_sub_${subAcy[key]}(str);
                        }
                        </#list>
                    </#if>
                    $("#view_sub_" + k).text(str);
                }
                $('#subViewDialog').dialog('open').dialog('setTitle', '查看');
            }

            //查看
            $("#subView").click(function () {
                subView();

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
            <#if tableConfig.subDelete>
            subDel = function (id) {
                //获取选中的ID串
                var ids;
                if (id) {
                    ids = id;
                } else {
                    ids = getSubDataGridSelectedDatas('${tableConfig.subTableConfig.primaryKey}');
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
                            url: "/project/${tableConfig.tableAliasName}/subTableManager",
                            data: {'ids': ids, 'op': 'delete'},
                            type: "Post",
                            dataType: "json",
                            success: function (data) {
                                $.messager.progress('close');
                                showMsg("删除成功");
                                subRefresh();
                                if (data.errcode != 0) {
                                    $.messager.alert("错误", data.errmsg, "error");
                                }
                            },
                            error: function (data) {
                                $.messager.progress('close');
                                $.messager.alert('服务器发生错误', "错误代码:"+data.status, "error");
                            }
                        });
                    }
                });
            };
            //删除
            $("#subDelete").click(function () {
                subDel();
            });
            </#if>
        </#if>
            init = false;
            setTimeout(function () {
                $.parser.parse();
            }, 1000);
        });
    </script>
</head>
<body>
<div id='Loading'
     style="position:absolute;z-index:1000;top:0px;left:0px;width:100%;height:100%;background:#DDDDDB;text-align:center;padding-top: 20%;">
    <h1><img src="/js/easyui/themes/gray/images/loading.gif"><font color="#15428B">加载中···</font></h1></div>
<table width="100%" id="dg" toolbar="#dg_tb"></table>
<div id="dg_tb" style="padding:3px; height: auto">
    <div style="height:30px;" class="datagrid-toolbar">
	<span style="float:left;">
    <aqu_add>
	<a id="add" href="javascript:" class="easyui-linkbutton" plain="true" icon="add">添加</a>
    </aqu_add>
        <aqu_edit>
	<a id="edit" href="javascript:" class="easyui-linkbutton" plain="true" icon="icon-edit">编辑</a>
        </aqu_edit>
        <aqu_delete>
	<a id="delete" href="javascript:" class="easyui-linkbutton" plain="true" icon="delete">删除</a>
        </aqu_delete>
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
                    <input id="search_${pojo.columnName}" ${inputStyle}  class="easyui-combobox"
                           name="${tableConfig.tableAliasName}.${pojo.columnName}"
                           data-options="
                     onShowPanel:function(){showCombboxData('search_${pojo.columnName}','${pojo.dictCode}')},
                    method:'get',
                    required:false,
                    valueField:'value',
                    textField:'label',
                    editable:false,
                    panelHeight:'auto'${validate}">
                <#else>
                    <input  ${inputStyle}  name="${tableConfig.tableAliasName}.${pojo.columnName}"
                                           data-options="required:false${validate}" class="f1 easyui-textbox"/>
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
<aqu_add>
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
                    <input id="add_${pojo.columnName}" ${inputStyle}  class="easyui-combobox"
                           name="${tableConfig.tableAliasName}.${pojo.columnName}"
                           data-options="
                     onShowPanel:function(){showCombboxData('add_${pojo.columnName}','${pojo.dictCode}')},
                    method:'get',
                    required:${pojo.required?c},
                    valueField:'value',
                    textField:'label',
                    editable:false,
                    panelHeight:'auto'${validate}">
                <#elseif pojo.inputType??&&pojo.inputType=="selectInput">
                    <input id="add_${pojo.columnName}" ${inputStyle}
                           name="${tableConfig.tableAliasName}.${pojo.columnName}" class="easyui-textbox"
                           data-options="editable:false,buttonText:'',buttonIcon:'icon-search',prompt:'',onClickButton:function(){pop('${tableConfig.tableAliasName}_${pojo.columnName}','${pojo.columnName}','add')}"
                           style="width:170px;height:24px;">
                <#elseif pojo.inputType??&&pojo.inputType=="datebox">
                    <input id="add_${pojo.columnName}"  ${inputStyle}
                           name="${tableConfig.tableAliasName}.${pojo.columnName}"
                           data-options="editable:false,required:${pojo.required?c}${validate}" class="f1 easyui-datebox"/>
                <#elseif pojo.inputType??&&pojo.inputType=="datetimebox">
                    <input id="add_${pojo.columnName}"  ${inputStyle}
                           name="${tableConfig.tableAliasName}.${pojo.columnName}"
                           data-options="editable:false,required:${pojo.required?c}${validate}" class="f1 easyui-datetimebox"/>
                <#else>
                    <input id="add_${pojo.columnName}"  ${inputStyle}
                           name="${tableConfig.tableAliasName}.${pojo.columnName}"
                           data-options="required:${pojo.required?c}${validate}" class="f1 easyui-textbox"/>
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
    </aqu_add>
<aqu_edit>
<div id="editDialog" class="easyui-dialog" style="width: 800px; height: auto; padding: 10px 20px"
     data-options="closed:true,buttons:'#editDialog-buttons',modal:true,iconCls:'icon-edit'">
    <form id="editForm" action="/systemManager/userManager" method="post">
        <input type="hidden" name="op" value="edit"/>
        <input type="hidden" name="${tableConfig.tableAliasName}.${tableConfig.primaryKey}" value="0"/>
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
                    <input id="edit_${pojo.columnName}" ${inputStyle}  class="easyui-combobox"
                           name="${tableConfig.tableAliasName}.${pojo.columnName}"
                           data-options="
                     onShowPanel:function(){showCombboxData('edit_${pojo.columnName}','${pojo.dictCode}')},
                    method:'get',
                    required:${pojo.required?c},
                    valueField:'value',
                    textField:'label',
                    editable:false,
                    panelHeight:'auto'${validate}">
                <#elseif pojo.inputType??&&pojo.inputType=="selectInput">
                    <input id="edit_${pojo.columnName}" ${inputStyle}
                           name="${tableConfig.tableAliasName}.${pojo.columnName}" class="easyui-textbox"
                           data-options="editable:false,buttonText:'',buttonIcon:'icon-search',prompt:'',onClickButton:function(){pop('${tableConfig.tableAliasName}_${pojo.columnName}','${pojo.columnName}','edit')}"
                           style="width:170px;height:24px;">
                <#elseif pojo.inputType??&&pojo.inputType=="datebox">
                    <input id="edit_${pojo.columnName}"  ${inputStyle}
                           name="${tableConfig.tableAliasName}.${pojo.columnName}"
                           data-options="editable:false,required:${pojo.required?c}${validate}" class="f1 easyui-datebox"/>
                <#elseif pojo.inputType??&&pojo.inputType=="datetimebox">
                    <input id="edit_${pojo.columnName}"  ${inputStyle}
                           name="${tableConfig.tableAliasName}.${pojo.columnName}"
                           data-options="editable:false,required:${pojo.required?c}${validate}" class="f1 easyui-datetimebox"/>
                <#else>
                    <input id="edit_${pojo.columnName}"  ${inputStyle}
                           name="${tableConfig.tableAliasName}.${pojo.columnName}"
                           data-options="required:${pojo.required?c}${validate}" class="f1 easyui-textbox"/>
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
</aqu_edit>
<div id="viewDialog" class="easyui-dialog" style="width: 800px; ${subTableDialogHeight} padding: 10px 20px"
     data-options="closed:true,buttons:'#viewDialog-buttons',modal:true,iconCls:'icon-search'">
    <table style="font-size: 12px;text-align: right" id="subable">
    <#list tableConfig.allDisplayPojos as pojo>
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
    <#if tableConfig.subAdd>
	<a id="subAdd" href="javascript:" class="easyui-linkbutton" plain="true" icon="add">添加</a>
        </#if>
        <#if tableConfig.subEdit>
	<a id="subEdit" href="javascript:" class="easyui-linkbutton" plain="true" icon="icon-edit">编辑</a>
        </#if>
        <#if tableConfig.subDelete>
	<a id="subDelete" href="javascript:" class="easyui-linkbutton" plain="true" icon="delete">删除</a>
        </#if>
	<a id="subView" href="javascript:" class="easyui-linkbutton" plain="true" icon="icon-search">查看</a>
        <a id="subRefresh" href="javascript:" class="easyui-linkbutton" plain="true" icon="arrowrefresh">刷新</a>

	</span>
            </div>
            <div name="searchPanel">

                <form id="subSearchForm" action="/systemManager/userManager" method="post">
                    <input type="hidden" name="op" value="view"/>
                    <table style="font-size: 12px;text-align: right">
                        <#list tableConfig.subTableConfig.allQueryPojos as pojo>
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
                                <input id="search_${pojo.columnName}" ${inputStyle}  class="easyui-combobox"
                                       name="${tableConfig.subTableConfig.tableAliasName}.${pojo.columnName}"
                                       data-options="
                     onShowPanel:function(){showCombboxData('search_${pojo.columnName}','${pojo.dictCode}')},
                    method:'get',
                    required:false,
                    valueField:'value',
                    textField:'label',
                    editable:false,
                    panelHeight:'auto'${validate}">
                            <#else>
                                <input  ${inputStyle}  name="${tableConfig.subTableConfig.tableAliasName}.${pojo.columnName}"
                                                       data-options="required:false${validate}" class="f1 easyui-textbox"/>
                            </#if>
                            <#if tdCount==3&&!pojo_has_next>
                                <a href="javascript:" id="subSearchButton" class="easyui-linkbutton" icon="find"></a><a
                                    href="javascript:" id="subResetButton" class="easyui-linkbutton" icon="erase"></a>
                            </td>
                            <#elseif !pojo_has_next>
                                </td>
                                <td></td>
                                <td><a href="javascript:" id="subSearchButton" class="easyui-linkbutton" icon="find">查询</a><a
                                        href="javascript:" id="subResetButton" class="easyui-linkbutton" icon="erase">重置</a></td>
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
    </div>
</#if>
    <div id="viewDialog-buttons">
        <a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-cancel',plain:true"
           onclick="javascript:$('#viewDialog').dialog('close')">取消</a>
    </div>
</div>
<#if tableConfig.subViewObjectId gt 0>
<div id="subdlgs">
    <#if tableConfig.subAdd>
    <div id="subAddDialog" class="easyui-dialog" style="width: 800px; height: auto; padding: 10px 20px"
         data-options="closed:true,buttons:'#subAddDialog-buttons',modal:true,iconCls:'icon-add'">
        <form id="subAddForm" action="/systemManager/userManager" method="post">
            <input type="hidden" name="op" value="add"/>
            <input type="hidden" name="${tableConfig.subTableConfig.tableAliasName}.${tableConfig.subViewLinkColumn}" value=""/>
            <table style="font-size: 12px;text-align: right">
                <#list tableConfig.subTableConfig.allAddPojos as pojo>
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
                            <input id="add_${pojo.columnName}" ${inputStyle}  class="easyui-combobox"
                                   name="${tableConfig.subTableConfig.tableAliasName}.${pojo.columnName}"
                                   data-options="
                     onShowPanel:function(){showCombboxData('add_${pojo.columnName}','${pojo.dictCode}')},
                    method:'get',
                    required:${pojo.required?c},
                    valueField:'value',
                    textField:'label',
                    editable:false,
                    panelHeight:'auto'${validate}">
                        <#elseif pojo.inputType??&&pojo.inputType=="selectInput">
                            <input id="add_${pojo.columnName}" ${inputStyle}
                                   name="${tableConfig.subTableConfig.tableAliasName}.${pojo.columnName}" class="easyui-textbox"
                                   data-options="editable:false,buttonText:'',buttonIcon:'icon-search',prompt:'',onClickButton:function(){subPop('${tableConfig.subTableConfig.tableAliasName}_${pojo.columnName}','${pojo.columnName}','subAdd')}"
                                   style="width:170px;height:24px;">
                        <#elseif pojo.inputType??&&pojo.inputType=="datebox">
                            <input id="add_${pojo.columnName}"  ${inputStyle}
                                   name="${tableConfig.subTableConfig.tableAliasName}.${pojo.columnName}"
                                   data-options="editable:false,required:${pojo.required?c}${validate}" class="f1 easyui-datebox"/>
                        <#elseif pojo.inputType??&&pojo.inputType=="datetimebox">
                            <input id="add_${pojo.columnName}"  ${inputStyle}
                                   name="${tableConfig.subTableConfig.tableAliasName}.${pojo.columnName}"
                                   data-options="editable:false,required:${pojo.required?c}${validate}" class="f1 easyui-datetimebox"/>
                        <#else>
                            <input id="add_${pojo.columnName}"  ${inputStyle}
                                   name="${tableConfig.subTableConfig.tableAliasName}.${pojo.columnName}"
                                   data-options="required:${pojo.required?c}${validate}" class="f1 easyui-textbox"/>
                        </#if>
                    </td>
                    <#if tdCount==3||!pojo_has_next>
                        <#assign tdCount=0>
                    </tr>
                    </#if>
                </#list>

            </table>
        </form>
        <div id="subAddDialog-buttons">
            <a href="javascript:void(0)" class="easyui-linkbutton" id="subAddSaveButton"
               data-options="iconCls:'icon-save',plain:true">保存</a>
            <a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-cancel',plain:true"
               onclick="javascript:$('#subAddDialog').dialog('close')">取消</a>
        </div>
    </div>
        </#if>
    <#if tableConfig.subEdit>
    <div id="subEditDialog" class="easyui-dialog" style="width: 800px; height: auto; padding: 10px 20px"
         data-options="closed:true,buttons:'#subEditDialog-buttons',modal:true,iconCls:'icon-edit'">
        <form id="subEditForm" action="/systemManager/userManager" method="post">
            <input type="hidden" name="op" value="edit"/>
            <input type="hidden" name="${tableConfig.subTableConfig.tableAliasName}.${tableConfig.subTableConfig.primaryKey}" value="0"/>
            <table style="font-size: 12px;text-align: right">
                <#list tableConfig.subTableConfig.allEditPojos as pojo>
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
                            <input id="edit_${pojo.columnName}" ${inputStyle}  class="easyui-combobox"
                                   name="${tableConfig.subTableConfig.tableAliasName}.${pojo.columnName}"
                                   data-options="
                     onShowPanel:function(){showCombboxData('edit_${pojo.columnName}','${pojo.dictCode}')},
                    method:'get',
                    required:${pojo.required?c},
                    valueField:'value',
                    textField:'label',
                    editable:false,
                    panelHeight:'auto'${validate}">
                        <#elseif pojo.inputType??&&pojo.inputType=="selectInput">
                            <input id="edit_${pojo.columnName}" ${inputStyle}
                                   name="${tableConfig.subTableConfig.tableAliasName}.${pojo.columnName}" class="easyui-textbox"
                                   data-options="editable:false,buttonText:'',buttonIcon:'icon-search',prompt:'',onClickButton:function(){subPop('${tableConfig.subTableConfig.tableAliasName}_${pojo.columnName}','${pojo.columnName}','subEdit')}"
                                   style="width:170px;height:24px;">
                        <#elseif pojo.inputType??&&pojo.inputType=="datebox">
                            <input id="edit_${pojo.columnName}"  ${inputStyle}
                                   name="${tableConfig.subTableConfig.tableAliasName}.${pojo.columnName}"
                                   data-options="editable:false,required:${pojo.required?c}${validate}" class="f1 easyui-datebox"/>
                        <#elseif pojo.inputType??&&pojo.inputType=="datetimebox">
                            <input id="edit_${pojo.columnName}"  ${inputStyle}
                                   name="${tableConfig.subTableConfig.tableAliasName}.${pojo.columnName}"
                                   data-options="editable:false,required:${pojo.required?c}${validate}" class="f1 easyui-datetimebox"/>
                        <#else>
                            <input id="edit_${pojo.columnName}"  ${inputStyle}
                                   name="${tableConfig.subTableConfig.tableAliasName}.${pojo.columnName}"
                                   data-options="required:${pojo.required?c}${validate}" class="f1 easyui-textbox"/>
                        </#if>
                    </td>
                    <#if tdCount==3||!pojo_has_next>
                        <#assign tdCount=0>
                    </tr>
                    </#if>
                </#list>

            </table>
        </form>
        <div id="subEditDialog-buttons">
            <a href="javascript:void(0)" class="easyui-linkbutton" id="subEditSaveButton"
               data-options="iconCls:'icon-save',plain:true">保存</a>
            <a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-cancel',plain:true"
               onclick="javascript:$('#subEditDialog').dialog('close')">取消</a>
        </div>
    </div>
    </#if>
    <div id="subViewDialog" class="easyui-dialog" style="width: 800px; padding: 10px 20px"
         data-options="closed:true,buttons:'#subViewDialog-buttons',modal:true,iconCls:'icon-search'">
        <table style="font-size: 12px;text-align: right">
            <#list tableConfig.subTableConfig.allDisplayPojos as pojo>
                <#assign tdCount=tdCount+1>
                <#if pojo_index%3==0&&pojo_has_next>
                <tr>
                </#if>
                <td>${pojo.displayName}:</td>
                <td class="tdRightPadding" id="view_sub_${pojo.columnName}"></td>
                <#if tdCount==3||!pojo_has_next>
                    <#assign tdCount=0>
                </tr>
                </#if>
            </#list>

        </table>


        <div id="subViewDialog-buttons">
            <a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-cancel',plain:true"
               onclick="javascript:$('#subViewDialog').dialog('close')">取消</a>
        </div>
    </div>
</div>
</#if>
<#if tableConfig.hasSelectInput||(tableConfig.subTableConfig??&&tableConfig.subTableConfig.hasSelectInput)>
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
                <a id="popRefreshButton" href="javascript:" class="easyui-linkbutton" icon="arrowrefresh">刷新</a>
            </form>
        </div>
    </div>

    <div id="popGridDialog-buttons">
        <a href="javascript:void(0)" class="easyui-linkbutton" id="popSelItButton"
           data-options="iconCls:'accept',plain:true">选择</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-cancel',plain:true"
           onclick="javascript:$('#popGrid').dialog('close')">取消</a>
    </div>
</div>
</#if>
</body>
</html>