// @flow
import { Advert } from 'commercial-control/modules/dfp/Advert';
import { prebid } from 'commercial-control/modules/prebid/prebid';

const loadAdvert = (advert: Advert): void => {
    advert.whenSlotReady
        .catch(() => {
            // The display needs to be called, even in the event of an error.
        })
        .then(() => {
            advert.startLoading();
            return prebid.requestBids(advert);
        })
        .then(() => {
            window.googletag.display(advert.id);
        });
};

export default loadAdvert;
