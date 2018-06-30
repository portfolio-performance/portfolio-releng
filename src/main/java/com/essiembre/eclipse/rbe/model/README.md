# Info

This package contains three classes adopted from https://github.com/essiembre/eclipse-rbe

The factories to read and write resource bundles have dependencies to Eclipse code (particularly AbstractUIPlugin via the preferences object) which do not work in a standalone CLI execution. The classes in here are replacements that cut those dependencies.