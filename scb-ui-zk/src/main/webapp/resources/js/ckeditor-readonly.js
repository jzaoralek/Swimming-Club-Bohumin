/**
 * 
 */
CKEDITOR.editorConfig = function(config) {
	config.resize_enabled = false;
	config.readOnly = true;
	config.toolbar = 'Complex';
	config.toolbar_Complex = [
			[ 'Undo', 'Redo' ],
			[ 'Bold', 'Italic', 'Underline', 'Subscript',
					'Superscript', 'TextColor', 'BGColor', '-', 'Cut', 'Copy',
					'Paste', '-', 'NumberedList',
					'BulletedList'],
			[ 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock' ],
			[ 'Format', 'Font', 'FontSize', 'Maximize' ] ];
};