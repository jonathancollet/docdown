/*
 * Copyright (C) 2013-2013 Nicolas Christe
 * Copyright (C) 2013-2013 Parrot S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.parrot.docdown.generator.html;

import com.parrot.docdown.data.markup.MarkupDoc;
import com.parrot.docdown.data.page.MarkupPage;
import com.parrot.docdown.generator.DefaultGenerator;
import com.parrot.docdown.generator.PageRenderer;
import org.rendersnake.HtmlCanvas;

import java.io.IOException;

public abstract class MarkupContentRenderable extends PageContentRenderable {

    public MarkupContentRenderable(DefaultGenerator generator) {
        super(generator);
    }

    @Override
    protected void doRenderOn(HtmlCanvas html) throws IOException {
        MarkupPage page = PageRenderer.getPage(html);
        MarkupDoc markupDoc = page.getMarkupDoc();
        renderContent(html, markupDoc);
    }

    public abstract void renderContent(HtmlCanvas html, MarkupDoc markupDoc) throws IOException;
}