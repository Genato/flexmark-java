/*
 * Copyright (c) 2015-2016 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.vladsch.flexmark.ast.util;

import com.vladsch.flexmark.ast.AnchorRefTarget;
import com.vladsch.flexmark.ast.Block;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ast.NodeVisitorBase;

/**
 * Abstract visitor that visits only children of blocks excluding Paragraphs
 * <p>
 * Can be used to only process block nodes efficiently skipping text. If you override a method and want visiting to descend into children,
 * call {@link #visitChildren}.
 */
public abstract class AnchorRefTargetBlockVisitor extends NodeVisitorBase {
    protected abstract void visit(AnchorRefTarget node);
    
    public void visit(Node node) {
        if (node instanceof AnchorRefTarget) visit((AnchorRefTarget) node);
        if (node instanceof Block) {
            visitChildren(node);
        }
    }
}
