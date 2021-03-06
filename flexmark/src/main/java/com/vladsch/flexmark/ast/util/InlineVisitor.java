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

import com.vladsch.flexmark.ast.*;

public interface InlineVisitor {
    void visit(final AutoLink node);
    void visit(final Code node);
    void visit(final Emphasis node);
    void visit(final HardLineBreak node);
    void visit(final HtmlEntity node);
    void visit(final HtmlInline node);
    void visit(final HtmlInlineComment node);
    void visit(final Image node);
    void visit(final ImageRef node);
    void visit(final Link node);
    void visit(final LinkRef node);
    void visit(final MailLink node);
    void visit(final SoftLineBreak node);
    void visit(final StrongEmphasis node);
    void visit(final Text node);
}
