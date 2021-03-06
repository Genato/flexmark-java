package com.vladsch.flexmark.ext.gfm.tasklist;

import com.vladsch.flexmark.ast.VisitHandler;
import com.vladsch.flexmark.ast.Visitor;

public class TaskListItemVisitorExt {
    static <V extends TaskListItemVisitor> VisitHandler<?>[] VISIT_HANDLERS(final V visitor) {
        return new VisitHandler<?>[] {
                new VisitHandler<>(TaskListItem.class, new Visitor<TaskListItem>() {
                    @Override
                    public void visit(TaskListItem node) {
                        visitor.visit(node);
                    }
                }),
        };
    }
}
