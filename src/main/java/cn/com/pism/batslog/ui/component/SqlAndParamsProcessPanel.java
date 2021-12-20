package cn.com.pism.batslog.ui.component;

import cn.com.pism.batslog.model.BslErrorMod;
import cn.com.pism.batslog.util.SqlFormatUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.GuiUtils;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author PerccyKing
 * @version 0.0.1
 * @date 2021/12/11 下午 09:07
 * @since 0.0.1
 */
@Data
public class SqlAndParamsProcessPanel {
    private JPanel root;
    private JPanel sqlContentLine;

    private Project project;

    private BslErrorMod bslErrorMod;

    public SqlAndParamsProcessPanel(Project project, BslErrorMod bslErrorMod) {
        this.project = project;
        this.bslErrorMod = bslErrorMod;

        String errorModSql = bslErrorMod.getSql();

        boolean lastNeedShow = errorModSql.endsWith("?");

        //生成SQL行
        final String[] sqlLines = errorModSql.split("\\?");
        List<String> encodedRowSpecs = new ArrayList<>();


        for (int i = 0; i < sqlLines.length; i++) {
            encodedRowSpecs.add("default");
        }

        FormLayout formLayout = new FormLayout("5dlu, right:default, 5dlu, 80dlu, 60dlu", String.join(",", encodedRowSpecs));
        sqlContentLine.setLayout(formLayout);

        String params = bslErrorMod.getParams();
        createProcessPanel(sqlLines, SqlFormatUtil.parseParamToList(params), lastNeedShow);

        GuiUtils.replaceJSplitPaneWithIDEASplitter(root);
    }

    private void createProcessPanel(String[] sqlLines, List<Object> paramsList, boolean lastNeedShow) {
        if (sqlLines.length >= paramsList.size()) {
            for (int i = 0; i < sqlLines.length; i++) {
                String line = sqlLines[i];
                boolean last = i == sqlLines.length - 1;
                if (line.length() > 50) {
                    line = "……" + line.substring(line.length() - 50);
                }
                String type = "";
                String paramsVal = "";
                if (i < paramsList.size()) {
                    Object param = paramsList.get(i);
                    type = param.getClass().getTypeName();
                    type = type.substring(type.lastIndexOf(".") + 1);
                    paramsVal = param.toString();
                }
                addLineToRow(i, line, type, paramsVal);
            }
        } else {
            for (int i = 0; i < paramsList.size(); i++) {
                Object param = paramsList.get(i);
            }
        }
    }


    private void addLineToRow(int i, String line, String type, String paramsVal) {
        final JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        jPanel.setSize(new Dimension(-1, Integer.MAX_VALUE));
        jPanel.add(new JLabel(line, SwingConstants.RIGHT));
        CellConstraints c = new CellConstraints();
        if (StringUtils.isNotBlank(line)) {
            sqlContentLine.add(jPanel, c.xy(2, i + 1));
        }
        if (StringUtils.isNotBlank(paramsVal)) {
            sqlContentLine.add(new JTextField(paramsVal), c.xy(4, i + 1));
            sqlContentLine.add(getSelection(type), c.xy(5, i + 1));
        }
    }

    private JComboBox<String> getSelection(String type) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.addItem("--请选择--");
        for (String ty : SqlFormatUtil.TYPES) {
            comboBox.addItem(ty);
        }
        if (StringUtils.isNotBlank(type)) {
            comboBox.setSelectedItem(type);
        }
        return comboBox;
    }

}
