import entryFactory from 'bpmn-js-properties-panel/lib/factory/EntryFactory';

import {
  is
} from 'bpmn-js/lib/util/ModelUtil';


export default function(group, element, translate) {

  // Only return an entry, if the currently selected
  // element is a start event.

  if (is(element, 'bpmn:Activity')) {
    group.entries.push(entryFactory.textField(translate, {
      id : 'expectedName',
      description : 'Expected Name',
      label : 'Expected Name',
      modelProperty : 'expectedName'
    }));
  }
}