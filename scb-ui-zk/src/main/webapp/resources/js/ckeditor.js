CKEDITOR.editorConfig = function(config) {
	config.resize_enabled = false;
	config.toolbar = 'Complex';
	config.toolbar_Simple = [ [ 'Bold', 'Italic', '-', 'NumberedList',
			'BulletedList', '-', 'Link', 'Unlink', '-', 'About' ] ];
	config.toolbar_Complex = [
			[ 'Undo', 'Redo' ],
			[ 'Bold', 'Italic', 'Underline', 'Strike', 'Subscript',
					'Superscript', 'TextColor', 'BGColor', '-', 'Cut', 'Copy',
					'Paste', 'Link', 'Unlink', 'Image' ],
			[ 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock' ],
			[ 'Table', 'SpecialChar', 'PageBreak', 'Styles', 'Format', 'Font',
					'FontSize', 'Maximize' ] ];
};