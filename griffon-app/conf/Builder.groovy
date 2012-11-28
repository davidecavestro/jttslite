


root.'GlazedlistsGriffonAddon'.addon=true

root.'OxbowGriffonAddon'.addon=true

root.'OxbowGriffonAddon'.controller=['ask','confirm','choice','error','inform','input','showException','radioChoice','warn']

jx {
    'groovy.swing.SwingXBuilder' {
        view = '*'
    }
}

root.'griffon.builder.css.CSSBuilder'.view = '*'
root.'griffon.builder.css.CSSBuilder'.controller = ['CSS']

root {
    'groovy.swing.SwingBuilder' {
        controller = ['Threading']
        view = '*'
    }
}
