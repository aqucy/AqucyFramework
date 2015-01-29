<script type="text/javascript" src="plug-in/jquery/jquery-1.8.3.js"></script>
<script type="text/javascript" src="plug-in/tools/dataformat.js"></script>
<link id="easyuiTheme" rel="stylesheet" href="plug-in/easyui/themes/default/easyui.css" type="text/css"></link>
<link rel="stylesheet" href="plug-in/easyui/themes/icon.css" type="text/css"></link>
<link rel="stylesheet" type="text/css" href="plug-in/accordion/css/accordion.css">
<link rel="stylesheet" type="text/css" href="plug-in/accordion/css/icons.css">
<script type="text/javascript" src="plug-in/easyui/jquery.easyui.min.1.3.2.js"></script>
<script type="text/javascript" src="plug-in/easyui/locale/zh-cn.js"></script>
<script type="text/javascript" src="plug-in/tools/syUtil.js"></script>
<script type="text/javascript" src="plug-in/My97DatePicker/WdatePicker.js"></script>
<link rel="stylesheet" href="plug-in/tools/css/common.css" type="text/css"></link>
<script type="text/javascript" src="plug-in/lhgDialog/lhgdialog.min.js"></script>
<script type="text/javascript" src="plug-in/tools/curdtools_zh-cn.js"></script>
<script type="text/javascript" src="plug-in/tools/easyuiextend.js"></script>
<script type="text/javascript">
    /**
     *表单的高度,表单的宽度
     **/
    var student_zuoyeFw = 700, student_zuoyeFh = 400;

    $(function () {
        $.get("cgFormHeadController.do?checkIsExit&checkIsTableCreate&name=student_zuoye",
                function (data) {
                    data = $.parseJSON(data);
                    if (data.success) {
                        createDataGridstudent_zuoye();
                    } else {
                        alertTip('表:<span style="color:red;">student_zuoye</span>还没有生成,请到表单配置生成表');
                    }
                });
    });

    function createDataGridstudent_zuoye() {
        var initUrl = 'cgAutoListController.do?datagrid&configId=student_zuoye&field=id,create_name,create_by,create_date,update_by,update_date,name,num,content,bpm_status,';
        initUrl = encodeURI(initUrl);
        $('#student_zuoyeList').datagrid(
                {

                    url: initUrl,
                    idField: 'id',
                    title: '学生作业',
                    fit: true,
                    fitColumns: true,
                    pageSize: 10,
                    pagination: true,
                    singleSelect: true,
                    sortName: 'create_date',
                    pageList: [10, 30, 50, 100],
                    sortOrder: 'desc',
                    rownumbers: true,
                    showFooter: true,
                    frozenColumns: [[]],
                    columns: [
                        [

                            {
                                field: 'id',
                                title: '主键',
                                hidden: true,
                                sortable: true,
                                width: 120
                            },
                            {
                                field: 'create_name',
                                title: '创建人真实名',
                                sortable: true,
                                width: 120
                            },
                            {
                                field: 'create_by',
                                title: '创建人CODE',
                                sortable: true,
                                width: 120
                            },
                            {
                                field: 'create_date',
                                title: '创建日期',
                                hidden: true,
                                sortable: true,
                                width: 120
                            },
                            {
                                field: 'update_by',
                                title: '修改人名称',
                                hidden: true,
                                sortable: true,
                                width: 120
                            },
                            {
                                field: 'update_date',
                                title: '修改日期',
                                hidden: true,
                                sortable: true,
                                width: 120
                            },
                            {
                                field: 'name',
                                title: '作业名',
                                sortable: true,
                                width: 120
                            },
                            {
                                field: 'num',
                                title: '作业本书',
                                sortable: true,
                                width: 120
                            },
                            {
                                field: 'content',
                                title: '作业描述',
                                sortable: true,
                                width: 120
                            },
                            {
                                field: 'bpm_status',
                                title: '流程状态',
                                sortable: true,
                                width: 120
                            },
                            {
                                field: 'opt', title: '操作', width: 200, formatter: function (value, rec, index) {
                                if (!rec.id) {
                                    return '';
                                }
                                var href = '';
                                href += "[<a href='#' onclick=delObj('cgAutoListController.do?del&configId=student_zuoye&id=" + rec.id + "','student_zuoyeList')>";
                                href += "删除</a>]";
                                if (rec.bpm_status != null && rec.bpm_status.indexOf('提交') > 0) {
                                    href += "[<a href='#' onclick=confirm('activitiController.do?startOnlineProcess&configId=student_zuoye&id=" + rec.id + "','确认提交流程','student_zuoyeList')>";
                                    href += "提交流程</a>]";
                                }

                                return href;
                            }
                            }
                        ]
                    ],
                    onLoadSuccess: function (data) {
                        $("#student_zuoyeList").datagrid("clearSelections");
                    },
                    onClickRow: function (rowIndex, rowData) {
                        rowid = rowData.id;
                        gridname = 'student_zuoyeList';
                    }
                });
        $('#student_zuoyeList').datagrid('getPager').pagination({
            beforePageText: '',
            afterPageText: '/{pages}',
            displayMsg: '{from}-{to}共{total}条',
            showPageList: true,
            showRefresh: true
        });
        $('#student_zuoyeList').datagrid('getPager').pagination({
            onBeforeRefresh: function (pageNumber, pageSize) {
                $(this).pagination('loading');
                $(this).pagination('loaded');
            }
        });
        //将没有权限的按钮屏蔽掉
    }
    //列表刷新
    function reloadTable() {
        try {
            $('#' + gridname).datagrid('reload');
            $('#' + gridname).treegrid('reload');
        } catch (ex) {
            //donothing
        }
    }
    //列表刷新-推荐使用
    function reloadstudent_zuoyeList() {
        $('#student_zuoyeList').datagrid('reload');
    }
    /**
     * 获取列表中选中行的数据-推荐使用
     * @param field 数据中字段名
     * @return 选中行的给定字段值
     */
    function getstudent_zuoyeListSelected(field) {
        var row = $('#student_zuoyeList').datagrid('getSelected');
        if (row != null) {
            value = row[field];
        } else {
            value = '';
        }
        return value;
    }
    /**
     * 获取列表中选中行的数据
     * @param field 数据中字段名
     * @return 选中行的给定字段值
     */
    function getSelected(field) {
        var row = $('#' + gridname).datagrid('getSelected');
        if (row != null) {
            value = row[field];
        } else {
            value = '';
        }
        return value;
    }
    /**
     * 获取列表中选中行的数据（多行）
     * @param field 数据中字段名-不传此参数则获取全部数据
     * @return 选中行的给定字段值，以逗号分隔
     */
    function getstudent_zuoyeListSelections(field) {
        var ids = '';
        var rows = $('#student_zuoyeList').datagrid('getSelections');
        for (var i = 0; i < rows.length; i++) {
            ids += rows[i][field];
            ids += ',';
        }
        ids = ids.substring(0, ids.length - 1);
        return ids;
    }
    /**
     * 列表查询
     */
    function student_zuoyeListsearch() {
        var queryParams = $('#student_zuoyeList').datagrid('options').queryParams;
        $('#student_zuoyeListtb').find('*').each(
                function () {
                    queryParams[$(this).attr('name')] = $(this).val();
                });
        $('#student_zuoyeList').datagrid({
            url: 'cgAutoListController.do?datagrid&configId=student_zuoye&field=id,create_name,create_by,create_date,update_by,update_date,name,num,content,bpm_status,',
            pageNumber: 1
        });
    }
    function dosearch(params) {
        var jsonparams = $.parseJSON(params);
        $('#student_zuoyeList').datagrid({
            url: 'cgAutoListController.do?datagrid&configId=student_zuoye&field=id,create_name,create_by,create_date,update_by,update_date,name,num,content,bpm_status,,',
            queryParams: jsonparams
        });
    }
    function student_zuoyeListsearchbox(value, name) {
        var queryParams = $('#student_zuoyeList').datagrid('options').queryParams;
        queryParams[name] = value;
        queryParams.searchfield = name;
        $('#student_zuoyeList').datagrid('reload');
    }
    $('#student_zuoyeListsearchbox').searchbox({
        searcher: function (value, name) {
            student_zuoyeListsearchbox(value, name);
        },
        menu: '#student_zuoyeListmm',
        prompt: '请输入查询关键字'
    });
    //查询重置
    function student_zuoyesearchReset(name) {
        $("#" + name + "tb").find("input[type!='hidden']").val("");
        student_zuoyeListsearch();
    }
    //将字段href中的变量替换掉
    function applyHref(tabname, href, value, rec, index) {
        //addOneTab(tabname,href);
        var hrefnew = href;
        var re = "";
        var p1 = /\#\{(\w+)\}/g;
        try {
            var vars = hrefnew.match(p1);
            for (var i = 0; i < vars.length; i++) {
                var keyt = vars[i];
                var p2 = /\#\{(\w+)\}/g;
                var key = p2.exec(keyt);
                hrefnew = hrefnew.replace(keyt, rec[key[1]]);
            }
        } catch (ex) {
        }
        re += "<a href = '#' onclick=\"addOneTab('" + tabname + "','" + hrefnew + "')\" ><u>" + value + "</u></a>";
        return re;
    }
    //SQL增强入口-按钮
    function doBusButton(url, content, gridname) {
        var rowData = $('#' + gridname).datagrid('getSelected');
        if (!rowData) {
            tip('请选择一条信息');
            return;
        }
        url = url + '&id=' + rowData.id;
        createdialog('确认 ', '确定' + content + '吗 ?', url, gridname);
    }
    //SQL增强入口-操作列里的链接
    function doBusButtonForLink(url, content, gridname, rowData) {
        if (!rowData) {
            tip('请选择一条信息');
            return;
        }
        url = url + '&id=' + rowData;
        createdialog('确认 ', '确定' + content + '吗 ?', url, gridname);
    }
    //新增
    function student_zuoyeadd() {
        add('学生作业录入', 'cgFormBuildController.do?ftlForm&tableName=student_zuoye', 'student_zuoyeList', student_zuoyeFw, student_zuoyeFh);
    }
    //修改
    function student_zuoyeupdate() {
        update('学生作业编辑', 'cgFormBuildController.do?ftlForm&tableName=student_zuoye', 'student_zuoyeList', student_zuoyeFw, student_zuoyeFh);
    }
    //查看
    function student_zuoyeview() {
        detail('查看', 'cgFormBuildController.do?ftlForm&tableName=student_zuoye&mode=read', 'student_zuoyeList', student_zuoyeFw, student_zuoyeFh);
    }

    //批量删除
    function student_zuoyedelBatch() {
        //获取选中的ID串
        var ids = getstudent_zuoyeListSelections('id');
        if (ids.length <= 0) {
            tip('请选择至少一条信息');
            return;
        }
        $.dialog.confirm('确定删除吗?', function (r) {
                    if (!r) {
                        return;
                    }
                    $.ajax({
                        url: "cgAutoListController.do?delBatch",
                        data: {'ids': ids, 'configId': 'student_zuoye'},
                        type: "Post",
                        dataType: "json",
                        success: function (data) {
                            tip(data.msg);
                            reloadstudent_zuoyeList();
                        },
                        error: function (data) {
                            $.messager.alert('错误', data.msg);
                        }
                    });
                }
        );
    }

    function student_zuoyeExportExcel() {
        var queryParams = $('#student_zuoyeList').datagrid('options').queryParams;
        $('#student_zuoyeListtb').find('*').each(function () {
            queryParams[$(this).attr('name')] = $(this).val();
        });
        var params = '&';
        $.each(queryParams, function (key, val) {
            params += '&' + key + '=' + val;
        });
        var fields = '&field=';
        $.each($('#student_zuoyeList').datagrid('options').columns[0], function (i, val) {
            if (val.field != 'opt') {
                fields += val.field + ',';
            }
        });
        window.location.href = "excelTempletController.do?exportXls&tableName=student_zuoye" + encodeURI(params + fields)
    }
    //JS增强

</script>
<table width="100%" id="student_zuoyeList" toolbar="#student_zuoyeListtb"></table>
<div id="student_zuoyeListtb" style="padding:3px; height: auto">
    <div name="searchColums">
    </div>
    <div style="height:30px;" class="datagrid-toolbar">
	<span style="float:left;">

	<a id="add" href="#" class="easyui-linkbutton" plain="true" icon="icon-add" onclick="student_zuoyeadd()">录入</a>
	<a id="update" href="#" class="easyui-linkbutton" plain="true" icon="icon-edit"
       onclick="student_zuoyeupdate()">编辑</a>
	<a id="delete" href="#" class="easyui-linkbutton" plain="true" icon="icon-remove" onclick="student_zuoyedelBatch()">批量删除</a>
	<a id="detail" href="#" class="easyui-linkbutton" plain="true" icon="icon-search"
       onclick="student_zuoyeview()">查看</a>
	<a id="import" href="#" class="easyui-linkbutton" plain="true" icon="icon-put"
       onclick="add('学生作业Excel数据导入','excelTempletController.do?goImplXls&tableName=student_zuoye','student_zuoyeList')">Excel数据导入</a>
	<a id="excel" href="#" class="easyui-linkbutton" plain="true" onclick="student_zuoyeExportExcel()"
       icon="icon-putout">Excel导出</a>

	</span>

    </div>
</div>

