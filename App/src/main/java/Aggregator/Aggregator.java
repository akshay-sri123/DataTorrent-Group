package Aggregator;

import com.apex.AdInfo;

import java.util.*;

public class Aggregator
{

	Converter converter=new Converter();

	protected final String[] keys;
	protected final String[] metrics;

	private final int id;
	private Map<List, List> aggMap = new HashMap<>();
	private Map<ByteArrayKeyValPair, ByteArrayKeyValPair> byteMap = new HashMap<>();
	
	public Aggregator(String[] keys, String[] metrics, int id) {
		this.keys = keys;
		this.metrics = metrics;
		this.id = id;
	}
	
	public void add(AdInfo adInfo) throws NoSuchFieldException, IllegalAccessException
	{


		byte[] keyByte = converter.getKeyBytes(id, adInfo);
    byte[] valByte = converter.getValueBytes(adInfo);
    ByteArrayKeyValPair pair = byteMap.get(new ByteArrayKeyValPair(keyByte, null));
    if(pair == null)
		{
      pair = new ByteArrayKeyValPair(keyByte, valByte);
			byteMap.put(pair, pair);
		}
		else
		{
			converter.updateByteValues(pair.getVal(), adInfo);
		}

  }

  private void updateValues(List vals, AdInfo adInfo)
  {
  	/*
  	0->cost
  	1->clicks
  	2->impressions
  	 */
    vals.set(0, (Double)vals.get(0) + adInfo.getCost());
    vals.set(1, (Integer)vals.get(1) + adInfo.getImpressions());
    vals.set(2, (Integer)vals.get(2) + (adInfo.isClicks() ? 1: 0));
  }

  private List getInitialValues(AdInfo adInfo)
  {
    List vals;
    vals = new ArrayList();
    //0->cost, 1->impressions, 2->click
    vals.add(adInfo.getCost());
    vals.add(adInfo.getImpressions());
    vals.add(adInfo.isClicks() ? 1 : 0);
    return vals;
  }

  private List getKey(AdInfo adInfo) throws NoSuchFieldException, IllegalAccessException {
    List key = new ArrayList();
    key.add(id);
    for(String keyStr : keys)
    {
      if(keyStr.equals("Publisher"))
      {
      	key.add(adInfo.getPublisher());
      }
      else if(keyStr.equals("Location"))
      {
      	key.add(adInfo.getLocation());
      }
      else if(keyStr.equals("Advertiser"))
      {
      	key.add(adInfo.getAdvertiser());
      }
    }
    return key;
  }
	byte[] readKey;
	byte[] readVal;
	public void dump()
	{
		for (Map.Entry<ByteArrayKeyValPair, ByteArrayKeyValPair> entry : byteMap.entrySet()) {
		  System.out.println(entry.getKey().toString());
            readKey=entry.getKey().getKey();
            readVal = entry.getKey().getVal();
            converter.readKeybytes(readKey);
            System.out.println("----------------------------------------");
            converter.readValuebytes(readVal);



		}
	}
}