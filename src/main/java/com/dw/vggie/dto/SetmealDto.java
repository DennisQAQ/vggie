package com.dw.vggie.dto;

import com.dw.vggie.entities.Setmeal;
import com.dw.vggie.entities.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
