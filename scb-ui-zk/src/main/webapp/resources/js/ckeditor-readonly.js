/**
 * 
 */
CKEDITOR.editorConfig = function(config) {
	config.resize_enabled = false;
	config.readOnly = true;
	config.removePlugins = 'elementspath';
	config.removePlugins = 'toolbar';
	config.toolbar = 'Complex';
	config.toolbar_Simple = [ ];
	config.toolbar_Complex = [ ];
};