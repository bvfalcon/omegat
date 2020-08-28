/* :name=Show Orphaned Segments :description=Display a list of segments where source is orphaned
 *
 * Display a list of segments where source is orphaned
 *
 * @author  Vladimir Bychkov
 * @date    2020-08-28
 * @version 0.1
 */

import groovy.swing.SwingBuilder
import java.awt.Component
import javax.swing.JButton
import javax.swing.JTable
import javax.swing.table.*
import javax.swing.event.*
import java.awt.event.*
import java.awt.BorderLayout as BL
import org.omegat.core.data.*

def prop = project.projectProperties
if (!prop) {
  final def title = 'Show orphaned segments'
  final def msg   = 'Please try again after you open a project.'
  showMessageDialog null, msg, title, INFORMATION_MESSAGE
  return
}


data = []

segment_count=0

iter = {
    String source, TMXEntry en -> 
        if (project.isOrphaned(source)) {
            data.add([ seg: segment_count+1, source: source])
            segment_count++;
        }
} as IProject.DefaultTranslationsIterator

project.iterateByDefaultTranslations(iter);

swing = new SwingBuilder()

frame = swing.frame(title:'Orphaned Segments', pack: true, show: true, preferredSize: [720, 500]) {
    scrollPane {
        table() {
            tableModel(list:data) {
                propertyColumn(editable: true, header:'№', propertyName:'seg', minWidth: 80, maxWidth: 80, preferredWidth: 80,
                        cellEditor: new TableCellEditor() {
                            public void cancelCellEditing()                             {}
                            public boolean stopCellEditing()                            {   return false;   }
                            public Object getCellEditorValue()                          {   return value;   }
                            public boolean isCellEditable(EventObject anEvent)          {   return true;    }
                            public boolean shouldSelectCell(EventObject anEvent)        {   return true;   }
                            public void addCellEditorListener(CellEditorListener l)     {}
                            public void removeCellEditorListener(CellEditorListener l)  {}
                            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
                            {
                                println("value: " + value);
                                org.omegat.core.Core.getEditor().gotoEntry(value);
                            }

                        },
                        cellRenderer: new TableCellRenderer()
                        {
                            public Component getTableCellRendererComponent(JTable table,
                            Object value,
                            boolean isSelected,
                            boolean hasFocus,
                            int row,
                            int column)
                            {
                                def btn = new JButton()
                                btn.setText(value.toString())
                                return btn

                            }
                        }
                        )
                propertyColumn(editable: false, header:'Source',propertyName:'source', preferredWidth: 200)
            }
        }
    }
   
        panel(constraints: BL.SOUTH){
            button('Quit', actionPerformed:{
                frame.visible = false
            })
	}
}
frame.pack()
frame.show()
