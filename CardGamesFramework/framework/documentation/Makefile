HTMLDOC="Documentation.html"

doc:
	doxygen Doxyfile
	if [ -a $(HTMLDOC) ]; then rm $(HTMLDOC) && ln -s docs/html/index.html Documentation.html; else ln -s docs/html/index.html Documentation.html; fi;
	#cd docs/latex && make
	#-mv docs/latex/refman.pdf ./ && mv refman.pdf Documentation.pdf && rm -rf docs/latex
	open Documentation.html

clean:
	rm -rf docs/latex docs/html && rm *.pdf *.html