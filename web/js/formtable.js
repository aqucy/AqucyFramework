com.v246.layout.FormTableLayout=Ext.extend(Ext.layout.ContainerLayout,{
    // private
    monitorResize:false,

    /**
     * Table列数
     */
    columns:2,
    /**
     * 是否自动排列
     *  若为true，则按行方向自动排列。
     *  若为false，则根据item指定的[row,col]排列，无row/col属性的项被忽略显示。
     */
    autoFlow:false,
    /**
     * Form Element显示模板
     */
    fieldTpl:
        new Ext.Template(
            '<div class="x-form-item {5}" tabIndex="-1">',
            '<label for="{0}" style="{2}" class="x-form-item-label">{1}{4}</label>',
            '<div class="x-form-element" id="x-form-el-{0}" style="{3}">',
            '</div><div class="{6}"></div>',
            '</div>'
        ),

    /**
     * 普通内容(非Form Element)显示模板
     */
    cellContentTpl:new Ext.Template('<div class="x-form-item-label">{0}</div>'),

    // private
    setContainer : function(ct){
        My.layout.FormTableLayout.superclass.setContainer.call(this, ct);
        this.currentRow = 0;
        this.currentColumn = 0;
        this.spanCells = [];
        this.maxCreatedRowIndex=-1;
    },

    // private
    onLayout : function(ct, target){
        var cs = ct.items.items, len = cs.length;
        if(!this.table){
            //target.addClass('x-table-layout-ct');
            this.table = target.createChild(
                {tag:'table', cls:'x-table-layout', border:1, cellspacing:3, cn: {tag: 'tbody'}}, null, true);

            if(!this.autoFlow){
                var items=[];
                for(var i=0;i<len;i++){
                    //如果设置了autoFlow为true，则无row、col属性的字段将无法显示。
                    if(typeof cs[i].row!='undefined' && typeof cs[i].col!='undefined'){
                        cs[i].__index=i;
                        items.push(cs[i]);
                    }
                }
                ct.items.items=items.sort(function(item1,item2){
                    if(item1.row==item2.row){
                        if(item1.col==item2.col){
                            return item1.__index-item2.__index;
                        }
                        return item1.col-item2.col;
                    }
                    return item1.row-item2.row;
                });
            }
            this.renderAll(ct, target);
        }
    },

    // private
    createRow : function(index){
        var row = this.table.tBodies[0].childNodes[index];
        if(!row){
            row = document.createElement('tr');
            this.table.tBodies[0].appendChild(row);
            this.maxCreatedRowIndex=Math.max(this.maxCreatedRowIndex,index);
        }
        return row;
    },

    // private
    getRow : function(index){
        if(!this.autoFlow){
            //确保创建index之前的row
            for(var i=Math.max(0,this.maxCreatedRowIndex+1);i<index;i++){
                this.createRow(i);
            }
        }
        return this.createRow(index);
    },

    // private
    getCellAt : function(c,row,col){
        if(this.spanCells[col] && this.spanCells[col][row]){
            return null;
        }
        //计算之前有多少spanCells
        var spanCount=0;
        for(var i=0;i<col;i++){
            if(this.spanCells[i] && this.spanCells[i][row]){
                spanCount++;
            }
        }
        var r=this.getRow(row);
        var cell=r.childNodes[col-spanCount];
        if(cell){
            return cell;
        }
        this.currentRow=row;
        //预设置nextCell之前的初始列索引
        this.currentColumn=spanCount-1;
        for(var i=spanCount;i<=col;i++){
            this.getNextCell(c);
        }
        return r.childNodes[col-spanCount];
    },

    // private
    getNextCell : function(c){
        var td = document.createElement('td'), row, colIndex;
        if(!this.columns){
            row = this.getRow(0);
        }else {
            colIndex = this.currentColumn;
            if(colIndex !== 0 && (colIndex % this.columns === 0)){
                this.currentRow++;
                colIndex = (c.colspan || 1);
            }else{
                colIndex += (c.colspan || 1);
            }

            //advance to the next non-spanning row/col position
            var cell = this.getNextNonSpan(colIndex, this.currentRow);
            this.currentColumn = cell[0];
            if(cell[1] != this.currentRow){
                this.currentRow = cell[1];
                if(c.colspan){
                    this.currentColumn += c.colspan - 1;
                }
            }
            row = this.getRow(this.currentRow);
        }
        if(c.colspan){
            td.colSpan = c.colspan;
        }
        td.className = 'x-table-layout-cell';
        if(c.rowspan){
            td.rowSpan = c.rowspan;
            var rowIndex = this.currentRow, colspan = c.colspan || 1;
            //track rowspanned cells to add to the column index during the next call to getNextCell
            for(var r = rowIndex+1; r < rowIndex+c.rowspan; r++){
                for(var col=this.currentColumn-colspan+1; col <= this.currentColumn; col++){
                    if(!this.spanCells[col]){
                        this.spanCells[col] = [];
                    }
                    this.spanCells[col][r] = 1;
                }
            }
        }
        row.appendChild(td);
        return td;
    },

    // private
    getNextNonSpan: function(colIndex, rowIndex){
        var c = (colIndex <= this.columns ? colIndex : this.columns), r = rowIndex;
        for(var i=c; i <= this.columns; i++){
            if(this.spanCells[i] && this.spanCells[i][r]){
                if(++c > this.columns){
                    //exceeded column count, so reset to the start of the next row
                    return this.getNextNonSpan(1, ++r);
                }
            }else{
                break;
            }
        }
        return [c,r];
    },

    // private
    renderItem : function(c, position, target){
        //target is form or other container
        if(c && !c.rendered ){
            var td=this.autoFlow?this.getNextCell(c):this.getCellAt(c,c.row,c.col);
            if(!td){
                //cell at[row,col] is spanCell，不渲染。(row,col设置不正确)
                return;
            }
            if(c.isFormField && c.inputType != 'hidden'){
                var args = [
                    c.id, c.fieldLabel,
                    c.labelStyle||this.labelStyle||'',
                    this.elementStyle||'',
                    typeof c.labelSeparator == 'undefined' ? this.labelSeparator : c.labelSeparator,
                    (c.itemCls||this.container.itemCls||'') + (c.hideLabel ? ' x-hide-label' : ''),
                    c.clearCls || 'x-form-clear-left'
                ];
                if(typeof position == 'number'){
                    position = target.dom.childNodes[position] || null;
                }
                if(position){
                    //this.fieldTpl.insertBefore(position, args);
                    this.fieldTpl.append(td, args);
                }else{
                    //this.fieldTpl.append(target, args);
                    this.fieldTpl.append(td, args);
                }
                c.render('x-form-el-'+c.id);
            }else {
                //My.layout.FormTableLayout.superclass.renderItem.apply(this, arguments);
                this.cellContentTpl.append(td,[c.text||c.html||c.value]);
            }
        }
    },

    // private
    isValidParent : function(c, target){
        return true;
    }
});
Ext.Container.LAYOUTS['formtable'] = My.layout.FormTableLayout;