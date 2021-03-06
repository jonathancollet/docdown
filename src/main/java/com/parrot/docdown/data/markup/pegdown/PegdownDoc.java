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

package com.parrot.docdown.data.markup.pegdown;

import com.parrot.docdown.data.markup.MarkupDoc;
import org.parboiled.Parboiled;
import org.pegdown.Extensions;
import org.pegdown.Parser;
import org.pegdown.Printer;
import org.pegdown.ast.ExpLinkNode;
import org.pegdown.ast.HeaderNode;
import org.pegdown.ast.RootNode;
import org.pegdown.ast.SuperNode;
import org.pegdown.plugins.PegDownPlugins;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class PegdownDoc extends MarkupDoc {

    private RootNode rootNode;
    private String title;

    public PegdownDoc(Path sourceFilePath, Path sourcePath) {
        super(sourceFilePath, sourcePath);
    }

    @Override
    public void processMarkup() throws IOException {
        // load file into a string
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(sourceFilePath, Charset.defaultCharset())) {
            for (; ; ) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line);
                sb.append('\n');
            }
            sb.append("\n\n");
        }

        // parse it
        /*
        PegDownPlugins.Builder pluginBuilder = PegDownPlugins.builder();
        Parser parser = Parboiled.createParser(Parser.class, Extensions.FENCED_CODE_BLOCKS | Extensions.WIKILINKS,
                1000L, Parser.DefaultParseRunnerProvider, pluginBuilder.build());
        */

        Parser parser = Parboiled.createParser(Parser.class, Extensions.FENCED_CODE_BLOCKS | Extensions.WIKILINKS,
                1000L, Parser.DefaultParseRunnerProvider, PegDownPlugins.builder().withPlugin(PluginParser.class)
                        .build());
        rootNode = parser.parse(sb.toString().toCharArray());
        loadTitle();
    }

    private void loadTitle() {
        rootNode.accept(new CollectTextVisitor() {
            @Override
            public void visit(HeaderNode node) {
                printer = new Printer();
                visit((SuperNode) node);
                title = printer.getString();
                printer = null;
                exit = true;
            }
        });
    }

    @Override
    public void loadIndex(final IReferenceProcessor processor) {
        rootNode.accept(new CollectTextVisitor() {
            @Override
            public void visit(HeaderNode node) {
                // stop at the first header
                exit = true;
            }

            @Override
            public void visit(ExpLinkNode node) {
                // process all explicit links
                printer = new Printer();
                visit((SuperNode) node);
                processor.processRef(printer.getString(), node.url);
                printer = null;
            }
        });
    }

    @Override
    public String getTitle() {
        return title;
    }

    ;

    public RootNode getRootNode() {
        return rootNode;
    }

}
