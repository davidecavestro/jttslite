


root.'GlazedlistsGriffonAddon'.addon=true





jx {
    'groovy.swing.SwingXBuilder' {
        view = '*'
    }
}

root.'griffon.builder.css.CSSBuilder'.view = '*'
root.'griffon.builder.css.CSSBuilder'.controller = ['CSS']



root.'OxbowGriffonAddon'.addon=true

root.'OxbowGriffonAddon'.controller=['ask','confirm','choice','error','inform','input','showException','radioChoice','warn']

root {
    'groovy.swing.SwingBuilder' {
        controller = ['Threading']
        view = '*'
    }
}
