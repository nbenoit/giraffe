# Makefile - Giraffe Application

SRC_DIR=src
CLS_DIR=class
DOC_DIR=doc

all: jc

jc:
	@javac -Xlint -cp $(CLS_DIR) -d $(CLS_DIR) `find $(SRC_DIR) -name '*.java'`

run: jc
	@java -cp $(CLS_DIR) Start

clean_doc:
	@rm -rf $(DOC_DIR)

doc: clean_doc
	@mkdir $(DOC_DIR)
	@javadoc -sourcepath $(SRC_DIR) -d $(DOC_DIR) giraffe giraffe.ui giraffe.ui.nodes

clean: clean_doc
	@rm -rf `find -name '*~'` `find $(CLS_DIR) -name '*.class'`
