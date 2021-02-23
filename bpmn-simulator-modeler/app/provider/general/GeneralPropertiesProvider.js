import inherits from 'inherits';

import PropertiesActivator from 'bpmn-js-properties-panel/lib/PropertiesActivator';

import generalProps from './parts/GeneralProps';

function createGeneralTabGroups(element, translate) {

    var generalTabGroups = {
        id: 'general',
        label: 'General',
        entries: []
    };

    generalProps(generalTabGroups, element, translate);

    return [
        generalTabGroups
    ];
}

export default function GeneralPropertiesProvider(
    eventBus, bpmnFactory, canvas,
    elementRegistry, translate) {

    PropertiesActivator.call(this, eventBus);

    this.getTabs = function (element) {

        var generalTab = {
            id: 'general',
            label: 'General',
            groups: createGeneralTabGroups(element, translate)
        };

        return [
            generalTab
        ];
    };
}

inherits(GeneralPropertiesProvider, PropertiesActivator);