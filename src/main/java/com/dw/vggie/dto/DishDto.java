package com.dw.vggie.dto;

import com.dw.vggie.entities.Dish;
import com.dw.vggie.entities.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
