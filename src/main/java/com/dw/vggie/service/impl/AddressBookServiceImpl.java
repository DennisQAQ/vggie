package com.dw.vggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dw.vggie.entities.AddressBook;
import com.dw.vggie.mapper.AddressBookMapper;
import com.dw.vggie.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
