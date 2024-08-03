package com.example.ssl.api;

import com.example.ssl.model.Laundries;
import com.example.ssl.model.LaundriesInfo;
import com.example.ssl.model.Laundry;
import com.example.ssl.model.LaundryInfo;

import java.util.List;

public interface ParserApi {
     LaundriesInfo getLaundries();
     Laundry getLaundry(String laundryId);
     LaundryInfo getLaundryInfo(String laundryId);
     Boolean checkSlot(String laundryId, String slotId);
     List<String> getAvailable(String laundryId);
}