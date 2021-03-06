package com.vladsch.flexmark.ext.anchorlink;

import com.vladsch.flexmark.Extension;
import com.vladsch.flexmark.ext.anchorlink.internal.AnchorLinkNodePostProcessor;
import com.vladsch.flexmark.ext.anchorlink.internal.AnchorLinkNodeRenderer;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.DataKey;

/**
 * Extension for anchor links
 * <p>
 * Create it with {@link #create()} and then configure it on the builders
 * ({@link com.vladsch.flexmark.parser.Parser.Builder#extensions(Iterable)},
 * {@link com.vladsch.flexmark.html.HtmlRenderer.Builder#extensions(Iterable)}).
 * </p>
 * <p>
 * The parsed anchorlink text is turned into {@link AnchorLink} nodes.
 * </p>
 */
public class AnchorLinkExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {
    public static final DataKey<Boolean> ANCHORLINKS_WRAP_TEXT = new DataKey<>("ANCHORLINKS_WRAP_TEXT", true);
    public static final DataKey<String> ANCHORLINKS_TEXT_PREFIX = new DataKey<>("ANCHORLINKS_TEXT_PREFIX", "");
    public static final DataKey<String> ANCHORLINKS_TEXT_SUFFIX = new DataKey<>("ANCHORLINKS_TEXT_SUFFIX", "");
    public static final DataKey<String> ANCHORLINKS_ANCHOR_CLASS = new DataKey<>("ANCHORLINKS_ANCHOR_CLASS", "");
    public static final DataKey<Boolean> ANCHORLINKS_SET_NAME = new DataKey<>("ANCHORLINKS_SET_NAME", false);
    public static final DataKey<Boolean> ANCHORLINKS_SET_ID = new DataKey<>("ANCHORLINKS_SET_ID", true);
    public static final DataKey<Boolean> ANCHORLINKS_NO_BLOCK_QUOTE = new DataKey<>("ANCHORLINKS_NO_BLOCK_QUOTE", false);

    private AnchorLinkExtension() {
    }

    public static Extension create() {
        return new AnchorLinkExtension();
    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.postProcessorFactory(new AnchorLinkNodePostProcessor.Factory(parserBuilder));
        //parserBuilder.postProcessorFactory(new AnchorLinkPostProcessor.Factory());
    }

    @Override
    public void extend(HtmlRenderer.Builder rendererBuilder, String rendererType) {
        if (rendererType.equals("JIRA") || rendererType.equals("YOUTRACK")) {
        } else if (rendererType.equals("HTML")) {
            rendererBuilder.nodeRendererFactory(new NodeRendererFactory() {
                @Override
                public NodeRenderer create(DataHolder options) {
                    return new AnchorLinkNodeRenderer(options);
                }
            });
        }
    }
}
