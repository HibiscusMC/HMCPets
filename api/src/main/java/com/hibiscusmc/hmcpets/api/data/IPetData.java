package com.hibiscusmc.hmcpets.api.data;

import com.hibiscusmc.hmcpets.api.model.CollarModel;
import com.hibiscusmc.hmcpets.api.model.SkinModel;
import com.hibiscusmc.hmcpets.api.model.registry.PetType;

import java.util.Map;

public interface IPetData {

    String category();
    String id();
    PetType type();

    Map<String, CollarModel> collars();
    Map<String, SkinModel> skins();

}
