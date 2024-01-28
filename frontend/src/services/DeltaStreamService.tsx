import IStore from "../types/Store.type";
import httpCommon from "./http-common";

export class DeltaStreamService {
    static getOrgData() {
        return httpCommon.get<Array<IStore>>("/deltastream/organization");
    }

}
