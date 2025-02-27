/**
 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 in compliance with the License. You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software distributed under the License
 is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 or implied. See the License for the specific language governing permissions and limitations under
 the License.
 **/
import {asLayerObject, withSortKeys} from "../../utils/utils.js";
import theme from "../../theme.js";

let directives = [
    {
        filter: [
            'any',
            ['==', ['get', 'highway'], 'motorway'],
            ['==', ['get', 'highway'], 'motorway_link'],
        ],
        'line-color': theme.bridgeLineMotorwayLineColor,
        'road-width': 12,
    },
    {
        filter: [
            'any',
            ['==', ['get', 'highway'], 'trunk'],
            ['==', ['get', 'highway'], 'trunk_link'],
        ],
        'line-color': theme.bridgeLineTrunkLineColor,
        'road-width': 8,
    },
    {
        filter: [
            'any',
            ['==', ['get', 'highway'], 'primary'],
            ['==', ['get', 'highway'], 'primary_link'],
        ],
        'line-color': theme.bridgeLinePrimaryLineColor,
        'road-width': 10,
    },
    {
        filter: [
            'any',
            ['==', ['get', 'highway'], 'secondary'],
            ['==', ['get', 'highway'], 'secondary_link'],
        ],
        'line-color': theme.bridgeLineSecondaryLineColor,
        'road-width': 8,
    },
    {
        filter: [
            'any',
            ['==', ['get', 'highway'], 'tertiary'],
            ['==', ['get', 'highway'], 'tertiary_link'],
        ],
        'line-color': theme.bridgeLineTertiaryLineColor,
        'road-width': 8,
    },
    {
        filter: ['==', ['get', 'highway'], 'unclassified'],
        'line-color': theme.bridgeLineUnclassifiedLineColor,
        'road-width': 4,
    },
    {
        filter: ['==', ['get', 'highway'], 'residential'],
        'line-color': theme.bridgeLineResidentialLineColor,
        'road-width': 4,
    },
    {
        filter: ['==', ['get', 'highway'], 'living_street'],
        'line-color': theme.bridgeLineLivingStreetLineColor,
        'road-width': 4,
    },
    {
        filter: ['==', ['get', 'highway'], 'service'],
        'line-color': theme.bridgeLineServiceLineColor,
        'road-width': 4,
    },
    {
        filter: ['==', ['get', 'highway'], 'track'],
        'line-color': theme.bridgeLineTrackLineColor,
        'road-width': 2,
    },
    {
        filter: ['==', ['get', 'highway'], 'raceway'],
        'line-color': theme.bridgeLineRacewayLineColor,
        'road-width': 4,
    },
    {
        filter: [
            'all',
            ['==', ['get', 'highway'], 'pedestrian'],
            ['!=', ['get', '$type'], 'Polygon'],
        ],
        'line-color': theme.bridgeLinePedestrianLineColor,
        'road-width': 2,
    },
]

export default asLayerObject(withSortKeys(directives), {
    id: 'bridge_line',
    source: 'baremaps',
    'source-layer': 'highway',
    type: 'line',
    layout: {
        visibility: 'visible',
        'line-cap': 'butt',
        'line-join': 'miter',
    },
    filter: ['any', ['==', ['get', 'bridge'], 'yes']],
});
