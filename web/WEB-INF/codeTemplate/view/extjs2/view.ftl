<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE8">
    <title></title>
    <link rel="stylesheet" type="text/css" href="/js/ext/resources/css/ext-all.css"/>
    <link rel="stylesheet" type="text/css" href="/js/ext/resources/css/xtheme-gray.css"/>
    <link rel="stylesheet" type="text/css" href="/css/icon.css"/>
    <script type="text/javascript" src="/js/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="/js/ext/ext-all.js"></script>
    <script type="text/javascript" src="/js/ext/source/locale/ext-lang-zh_CN.js"></script>
    <style>
        .x-date-middle {
            padding-top:2px;padding-bottom:2px;
            width:130px; /* FF3 */
        }
    </style>
    <script type="text/javascript">
        Ext.onReady(function () {
            Ext.BLANK_IMAGE_URL = "/js/ext/resources/images/default/s.gif";
            Ext.QuickTips.init();
            Ext.form.Field.prototype.msgTarget = "qtip";//side:右侧 under:下方  id: [element id]错误提示显示在指定id的HTML元件中 qtip:鼠标移动到组件时显示
            var opUrl = "/project/${tableConfig.tableName}";
            var delUrl = opUrl;
            var viewUrl = opUrl;
            var addurl = opUrl;
            var editUrl = opUrl;
            var dictUrl = "/getDict?code=";
            var loadMarsk = new Ext.LoadMask(document.body, {
                msg: '正在加载数据，请稍候......',
                removeMask: true// 完成后移除
            });
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
///////////////////////////Store区///////////////////////////////////
            var store_grid = new Ext.data.JsonStore({
                url: viewUrl+'?op=view', //后缀名为json的文件
                totalProperty: "totalCount",
                root: 'datas',     //读取里面的users对象
                fields: [${acy['columnStr']}] //读取里面的属性
            });
            <#if tableConfig.dicts?exists>
                <#list  tableConfig.dicts?keys as key>
            var store_${acy[key]} = new Ext.data.JsonStore({
                url: dictUrl+'${key}',
                root: 'datas',
                fields: ['label', 'value']
            });
                </#list>
            </#if>

            //定义勾选框，不需要可不必定义
            var sm = new Ext.grid.CheckboxSelectionModel();
            //定义列,在这里定义每列的宽度
            var colM = new Ext.grid.ColumnModel([
                new Ext.grid.RowNumberer(),//增加自动编号，不需要可不必定义
                sm,//勾选框，不需要可不必定义
                    <#list tableConfig.columnPojos as pojo>
                    <#if pojo.displayName??>
                {
                    header: '${pojo.displayName}',
                    <#if pojo.dictCode??>
                        renderer: render_${acy[pojo.dictCode]},
                    </#if>
                    dataIndex: '${pojo.columnName}'
                },
                    </#if>
                    </#list>
                {
                    header: 'id',
                    hidden:true,
                    dataIndex: 'id'
                }
            ]);

            //生成表格
            var grid = new Ext.grid.GridPanel({
                loadMask: {
                    msg: '数据加载中，请稍候...'
                },
                cm: colM,
                sm: sm,
                store: store_grid,
                viewConfig: {
                    forceFit: true
                },
                tbar: [
                    {
                        text: "添加",
                        iconCls: "add",
                        handler: function (e) {
                            add();
                        }
                    },
                    {
                        text: "修改",
                        iconCls: "pageedit",
                        handler: function (e) {
                            edit();
                        }
                    },
                    {
                        text: "删除",
                        iconCls: "delete",
                        handler:function(e){
                            del();
                        }
                    },
                    {
                        text: "刷新",
                        iconCls: "arrowrefresh",
                        handler:function(e){
                            store_grid.reload();
                        }
                    }
                ],
                bbar: new Ext.PagingToolbar({
                    pageSize: 3,
                    store: store_grid,
                    displayInfo: true,
                    displayMsg: '显示第{0}条到{1}条记录,一共{2}条',
                    emptyMsg: '没有记录'
                })
            });
            grid.on("rowdblclick", function (datagrid, row) {
                edit();
            });
            grid.on("rowcontextmenu", function (grid, rowIndex, e) {
                if (rowIndex < 0) {
                    return;
                }
                e.preventDefault();
                var allowEdit = true;
                var selArr = grid.getSelectionModel().getSelections();
                if (selArr.length <= 1) {
                    //选中行
                    grid.getSelectionModel().selectRow(rowIndex,false);
                } else if (selArr.length > 1) {
                    allowEdit = false;
                }

                var menus = new Ext.menu.Menu
                ({
                    items: [
                        {
                            text: "添加",
                            pressed: true,
                            iconCls: "add",
                            handler: function () {
                                add();
                                menus.destroy();
                            }
                        },
                        {
                            text: "编辑",
                            pressed: true,
                            disabled:!allowEdit,
                            iconCls: "pageedit",
                            handler: function () {
                                edit();
                                menus.destroy();
                            }
                        },
                        {
                            text: "删除",
                            pressed: true,
                            iconCls: "delete",
                            handler: function () {
                                del();
                                menus.destroy();
                            }
                        },
                        {
                            text: "刷新",
                            pressed: true,
                            iconCls: "arrowrefresh",
                            handler: function () {
                                store_grid.reload();
                            }
                        }
                    ]
                });
                menus.showAt(e.getPoint());
            });
            var viewport = new Ext.Viewport({
                layout: 'border',
                autoScroll: true,
                items: [{
                    region: 'center',
                    deferredRender: false,
                    bodyStyle: "overflow-y:hidden;overflow-x:auto",
                    items: grid,
                    autoScroll: true
                }]
            });
//////////////////////查询界面/////////////////////
            var query_form = new Ext.FormPanel({
                renderTo: grid.tbar,
                labelAlign: 'left',
                buttonAlign: 'right',
                border: false,
                frame: true,
                items: [
                    {
                        layout: 'table',
                        layoutConfig: {
                            columns: 4
                        },
                        border: false,
                        labelSeparator: ':',
                        defaults: {
                            layout: 'form',
                            labelWidth: 12 * ${tableConfig.maxQueryColumnLen},
                            bodyStyle: 'padding:0 0 0 20',
                            border: false
                        },
                        items: [

                            <#list tableConfig.columnPojos as pojo>
                                <#if pojo.allowQuery>
                                    <#if pojo.dictCode??>
                                        {
                                            items: [{
                                                xtype: "combo",
                                                fieldLabel: "${pojo.displayName}",
                                                labelStyle : "text-align:right;width: ${12*tableConfig.maxQueryColumnLen};",
                                                hiddenName: "${tableConfig.tableName}.${pojo.columnName}",
                                                name: "${pojo.columnName}",
                                                allowBlank: ${(!pojo.required)?c},
                                                blankText: "不能为空，请填写",
                                                valueField: "value",
                                                displayField: "label",
                                                emptyText: "请选择...",
                                                mode: 'remote',
                                                forceSelection: true,
                                                triggerAction: 'all',
                                                width: 70,
                                                store: store_${pojo.columnName}
                                            }]
                                        },
                                    <#else>
                                        {
                                            items: [
                                                {
                                                    xtype: 'field',
                                                    maxlength: 15,
                                                    minLength: 5,
                                                    name: "${tableConfig.tableName}.${pojo.columnName}",
                                                    labelStyle : "text-align:right;width: ${12*tableConfig.maxQueryColumnLen};",
                                                    fieldLabel: "${pojo.displayName}"
                                                }
                                            ]
                                        },
                                    </#if>
                                </#if>
                            </#list>
                             {
                                layout: "table",
                                items: [
                                    {
                                        xtype: "button",
                                        text: "查询",
                                        iconCls: "find",
                                        handler: function () {
                                            //表单提交
                                            query(query_form);
                                        }
                                    }, {
                                        xtype: "button",
                                        text: "重置",
                                        iconCls: "erase",
                                        handler: function () {
                                            //表单提交
                                            query_form.form.reset();
                                            query(query_form);
                                        }
                                    },
                                    {
                                        xtype: 'hidden',
                                        name: "op",
                                        value: "view",
                                        fieldLabel: 'op'
                                    }
                                ]
                            }
                        ]
                    }
                ]//items
            })//FormPanel

            grid.setHeight(Ext.get("content").getHeight() - 5);
            store_grid.load();

//////////////////////编辑界面//////////////////////////////////////////
            function edit() {
                var selArr = grid.getSelectionModel().getSelections();
                if (selArr.length < 1) {
                    Ext.MessageBox.alert("", "请选择要编辑或修改的数据行!");
                    return;
                } else if (selArr.length > 1) {
                    Ext.MessageBox.alert("", "编辑或修改数据的时候不能多选!");
                    return;
                }
                json = selArr[0].json;
                var p = new Ext.FormPanel
                ({
                    frame: true, labelWidth: 12 * ${tableConfig.maxDisplayColumnLen},
                    items: [
                    <#list tableConfig.columnPojos as pojo>
                        <#if pojo.displayName??>
                            <#if pojo.dictCode??>
                                {
                                    xtype: "combo",
                                    fieldLabel: "${pojo.displayName}",
                                    labelStyle : "text-align:right;width: ${12*tableConfig.maxDisplayColumnLen};",
                                    id: "${pojo.columnName}",
                                    hiddenName: "${tableConfig.tableName}.${pojo.columnName}",
                                    name: "${pojo.columnName}",
                                    allowBlank: ${(!pojo.required)?c},
                                    blankText: "不能为空，请填写",
                                    valueField: "value",
                                    displayField: "label",
                                    emptyText: "请选择...",
                                    mode: 'remote',
                                    forceSelection: true,
                                    triggerAction: 'all',
                                    width: 70,
                                    store: store_${pojo.columnName}
                                },
                            <#else>
                                {
                                    xtype: "textfield",
                                    fieldLabel: "${pojo.displayName}",
                                    labelStyle : "text-align:right;width: ${12*tableConfig.maxDisplayColumnLen};",
                                    id: "${pojo.columnName}",
                                    hiddenName: "${tableConfig.tableName}.${pojo.columnName}",
                                <#if pojo.required==true>
                                    allowBlank: ${(!pojo.required)?c},
                                    blankText: "不能为空，请填写",
                                </#if>
                                <#if pojo.regex??>
                                    regex:/${pojo.regex}/,
                                            regexText:'${pojo.regexErrorMsg!}',
                                </#if>
                                    value:json.${pojo.columnName},
                                    width: 201
                                },
                            </#if>
                        </#if>
                    </#list>
                        {
                            xtype: "hidden",
                            id: "id",
                            name: "${tableConfig.tableName}.id",
                            width: 201,
                            value: json.id
                        },
                        {
                            xtype: "hidden",
                            id: "op",
                            name: "op",
                            width: 201,
                            value: "edit"
                        }
                    ]
                });
                <#if tableConfig.dicts?exists>
                    <#list  tableConfig.dicts?keys as key>
                store_${acy[key]}.on("load", function () {
                    comb = Ext.getCmp("${acy[key]}");
                    comb.setValue(json.${acy[key]});
                });
                store_${acy[key]}.load();
                    </#list>
                </#if>
                var win = new Ext.Window
                ({
                    title: "编辑窗口",
                    autoHeight: true,
                    width: 330,
                    resizable: false,
                    buttonAlign: "center",
                    modal: true,//height:300,
                    items: [p],
                    bbar: [
                        {
                            xtype: "button",
                            text: "确定",
                            iconCls: "accept",
                            handler: function () {
                                //表单提交
                                save(p, win,"edit");
                            }
                        },
                        '', '',
                        {
                            xtype: "button",
                            text: "关闭",
                            iconCls: "cancel",
                            handler: function () {
                                win.destroy();
                            }
                        }
                    ]
                });
                win.show();
            }

//////////////////////添加界面//////////////////////////////////////////
            function add() {
                var p = new Ext.FormPanel
                ({
                    frame: true,
                    labelWidth: 12 * ${tableConfig.maxDisplayColumnLen},
                    items: [
                    <#list tableConfig.columnPojos as pojo>
                        <#if pojo.displayName??>
                        <#if pojo.dictCode??>
                        {
                            xtype: "combo",
                            fieldLabel: "${pojo.displayName}",
                            labelStyle : "text-align:right;width: ${12*tableConfig.maxDisplayColumnLen};",
                            id: "${pojo.columnName}",
                            hiddenName: "${tableConfig.tableName}.${pojo.columnName}",
                            name: "${pojo.columnName}",
                            allowBlank: ${(!pojo.required)?c},
                            blankText: "不能为空，请填写",
                            valueField: "value",
                            displayField: "label",
                            emptyText: "请选择...",
                            mode: 'remote',
                            forceSelection: true,
                            triggerAction: 'all',
                            width: 70,
                            store: store_${pojo.columnName}
                        },
                        <#else>
                            {
                                xtype: "textfield",
                                fieldLabel: "${pojo.displayName}",
                                labelStyle : "text-align:right;width: ${12*tableConfig.maxDisplayColumnLen};",
                                id: "${pojo.columnName}",
                                name: "${tableConfig.tableName}.${pojo.columnName}",
                                <#if pojo.required==true>
                                    allowBlank: ${(!pojo.required)?c},
                                    blankText: "不能为空，请填写",
                                </#if>
                                <#if pojo.regex??>
                                    regex:/${pojo.regex}/,
                                    regexText:'${pojo.regexErrorMsg!}',
                                </#if>
                                width: 201
                            },
                        </#if>
                        </#if>
                    </#list>
                        {
                            xtype: "hidden",
                            id: "op",
                            name: "op",
                            width: 201,
                            value: "add"
                        }
                    ]
                });

                var win = new Ext.Window
                ({
                    title: "新增窗口",
                    autoHeight: true,
                    width: 320,
                    resizable: false,
                    buttonAlign: "center",
                    modal: true,//height:300,
                    items: [p],
                    bbar: [
                        {
                            xtype: "button",
                            text: "确定",
                            iconCls: "accept",
                            handler: function () {
                                //表单提交
                                save(p, win,"add");
                            }
                        },
                        '', '',
                        {
                            xtype: "button",
                            text: "关闭",
                            iconCls: "cancel",
                            handler: function () {
                                win.destroy();
                            }
                        }
                    ]
                });
                win.show();
            }

            function save(aquForm, aquWin,op) {
                //验证合法后使用加载进度条
                if (aquForm.form.isValid()) {
                    //提交到服务器操作
                    aquForm.form.submit({
                        waitMsg: '操作进行中,请稍后...',
                        url: op=="edit"?editUrl:addurl,
                        method: 'post',
                        //提交成功的回调函数
                        success: function (form, action) {
                            if (action.result.errcode == 0) {
                                store_grid.reload();
                                aquWin.destroy();
                            } else {
                                Ext.Msg.alert("", action.result.errmsg);
                            }
                        },
                        //提交失败的回调函数
                        failure: function (form, action) {
                            switch (action.failureType) {
                                case Ext.form.Action.CLIENT_INVALID:
                                    Ext.Msg.alert('错误提示', '表单数据非法请核实后重新输入！');
                                    break;
                                case Ext.form.Action.CONNECT_FAILURE:
                                    Ext.Msg.alert('错误提示', '网络连接异常！');
                                    break;
                                case Ext.form.Action.SERVER_INVALID:
                                    Ext.Msg.alert('错误提示', "您的输入用户信息有误，请核实后重新输入！");
                                    simple.form.reset();
                            }
                        }
                    });
                }
            };
            function query(aquForm) {
                //验证合法后使用加载进度条
                if (aquForm.form.isValid()) {
                    //提交到服务器操作
                    store_grid.baseParams = query_form.form.getValues();
                    aquForm.form.submit({
                        waitMsg: '操作进行中,请稍后...',
                        url: viewUrl,
                        method: 'post',
                        //提交成功的回调函数
                        success: function (form, action) {
                            if (action.result.errcode == 0) {
                                store_grid.loadData(action.result);
                            } else {
                                Ext.Msg.alert("", action.result.errmsg);
                            }
                        },
                        //提交失败的回调函数
                        failure: function (form, action) {
                            switch (action.failureType) {
                                case Ext.form.Action.CLIENT_INVALID:
                                    Ext.Msg.alert('错误提示', '表单数据非法请核实后重新输入！');
                                    break;
                                case Ext.form.Action.CONNECT_FAILURE:
                                    Ext.Msg.alert('错误提示', '网络连接异常！');
                                    break;
                                case Ext.form.Action.SERVER_INVALID:
                                    Ext.Msg.alert('错误提示', "您的输入用户信息有误，请核实后重新输入！");
                            }
                        }
                    });
                }
            };
            function del(){
                var selModel = grid.getSelectionModel();
                if (selModel.hasSelection()) {
                    Ext.Msg.confirm("警告", "确定要删除吗？", function (button) {
                        if (button == "yes") {
                            var selected = grid.getSelectionModel().getSelections();
                            var ids = []; //要删除的id
                            Ext.each(selected, function (item) {
                                ids.push(item.json.id);
                            });
                            loadMarsk.show();
                            Ext.Ajax.request({
                                url: delUrl+'?op=delete&ids=' + ids,
                                //根据id删除节点
                                success: function (request) {
                                    store_grid.reload();
                                    loadMarsk.hide();
                                },
                                failure: function () {
                                    alert("删除失败");
                                    loadMarsk.hide();
                                }
                            });
                        }
                    });
                }
                else {
                    Ext.Msg.alert("错误", "没有任何行被选中，无法进行删除操作！");
                }
            }
        });
    </script>
</head>
<body>
<div id="content" style="width: 100%;height: 100%"></div>
</body>
</html>