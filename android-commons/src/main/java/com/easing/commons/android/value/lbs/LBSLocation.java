package com.easing.commons.android.value.lbs;


import com.easing.commons.android.format.TimeUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class LBSLocation {
  public double latitude;
  public double longitude;
  public double altitude;
  public String address;
  public String time;

  public LBSLocation(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.time = TimeUtil.now();
  }

  @Override
  public String toString() {
    return "经度：" + longitude + "\n纬度：" + latitude + "\n地址：" + address + "\n时间：" + time;
  }
}
