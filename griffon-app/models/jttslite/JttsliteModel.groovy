package jttslite

import ca.odell.glazedlists.EventList
import ca.odell.glazedlists.BasicEventList
import ca.odell.glazedlists.SortedList

class JttsliteModel {

    @Bindable boolean inProgress
    @Bindable Workspace workspace

    EventList tasks = new SortedList( new BasicEventList(), {a, b -> a.name <=> b.name} as Comparator)

}