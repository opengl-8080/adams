$(function() {
    var searchDefinition = new SearchDefinition();
    var parameters = new Parameters();
});

function Parameters() {
    var _$area = $('#parameters');
    var _firstParameter = new Parameter({
        parent: _$area,
        parameterIndex: 0
    });

    var _parameterIndex = 0;
    var _parameters = [];

    $('#addParameterButton').on('click', function() {
        var newParameter = new Parameter({
            parent: _$area,
            parameterIndex: ++_parameterIndex,
            onRemoveParameter: function() {
                _parameters = _.reject(_parameters, function(parameter) {
                    return newParameter === parameter;
                });
            }
        });

        _parameters.push(newParameter);
    });
}

function Parameter(option) {
    var $parent = option.parent,
        parameterIndex = option.parameterIndex,
        onRemoveParameter = option.onRemoveParameter
    ;

    $parent.append(_html());
    var _$area = $('#parameterDefinition-' + parameterIndex);
    var _$removeParameterButton = $('#removeParameterButton-' + parameterIndex);

    if (parameterIndex === 0) {
        _$removeParameterButton.remove();
    } else {
        _$removeParameterButton.on('click', function() {
            _$area.remove();
            onRemoveParameter();
        });
    }

    function _html() {
        var template = $('#parameterTemplate').html();
        return Mustache.render(template, {
            parameterIndex: parameterIndex
        });
    }
}

function SearchDefinition() {
    var _$area = $('#searchDefinition');
    var _firstTable = new TableDefinition({
        parent: _$area, tableIndex: 0
    });
    var _tableIndex = 0;
    var _tables = [];

    $('#addTableButton').on('click', function() {
        var newTable = new TableDefinition({
            parent: _$area,
            tableIndex: ++_tableIndex,
            onRemoveTable: function() {
                _tables = _.reject(_tables, function(table) {
                    return newTable === table;
                });
            }
        });

        _tables.push(newTable);
    });
}

function TableDefinition(option) {
    var $parent = option.parent,
        tableIndex = option.tableIndex,
        onRemoveTable = option.onRemoveTable
        ;

    $parent.append(_html());

    var _$area = $('#tableDefinition-' + tableIndex);
    var _$columnDefinitions = $('#columnDefinitions-' + tableIndex);

    var _firstColumn = new ColumnDefinition({
        parent: _$columnDefinitions, tableIndex: tableIndex, columnIndex: 0
    });
    var _columnIndex = 0;
    var _columns = [];

    var _$removeTableButton = $('#removeTableButton-' + tableIndex);
    if (tableIndex === 0) {
        _$removeTableButton.remove();
    } else {
        _$removeTableButton.on('click', function() {
            _$area.remove();
        });
    }

    var _$addColumnButton = $('#addColumnButton-' + tableIndex);

    _$addColumnButton.on('click', function() {
        var newColumn = new ColumnDefinition({
            parent: _$columnDefinitions,
            tableIndex: tableIndex,
            columnIndex: ++_columnIndex,
            onRemoveColumn: function() {
                _columns = _.reject(_columns, function(column) {
                    return column === newColumn;
                });
            }
        });

        _columns.push(newColumn);
    });

    function _html() {
        var template = $('#tableTemplate').html();

        return Mustache.render(template, {
            tableIndex: tableIndex
        });
    }
}

function ColumnDefinition(option) {
    var $parent = option.parent,
        tableIndex = option.tableIndex,
        columnIndex = option.columnIndex,
        onRemoveColumn = option.onRemoveColumn
    ;

    $parent.append(_html());

    var _index = tableIndex + '-' + columnIndex;
    var _$area = $('#columnDefinition-' + _index);

    var _$removeColumnButton = $('#removeColumnButton-' + _index);
    if (columnIndex === 0) {
        _$removeColumnButton.remove();
    } else {
        _$removeColumnButton.on('click', function() {
            _$area.remove();
            onRemoveColumn();
        });
    }

    var _$equalSection = $('#equalSection-' + _index);
    var _$betweenSection = $('#betweenSection-' + _index);
    var _$compareType = $('#compareType-' + _index);
    var _$equalType = $('#equalType-' + _index);
    var _$parameterSection = $('#parameterSection-' + _index);
    var _$searchResultSection = $('#searchResultSection-' + _index);

    _$compareType.on('change', function() {
        var compareType = _$compareType.val();

        if (compareType === 'equal') {
            _$equalSection.show();
            _$betweenSection.hide();
        } else {
            _$equalSection.hide();
            _$betweenSection.show();
        }
    });

    _$equalType.on('change', function() {
        var equalType = _$equalType.val();

        if (equalType === 'parameter') {
            _$parameterSection.show();
            _$searchResultSection.hide();
        } else {
            _$parameterSection.hide();
            _$searchResultSection.show();
        }
    });

    function _html() {
        var template = $('#columnTemplate').html();
        return Mustache.render(template, {
            tableIndex: tableIndex,
            columnIndex: columnIndex
        });
    }
}