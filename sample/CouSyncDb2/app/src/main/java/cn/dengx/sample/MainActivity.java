package cn.dengx.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import cn.dengx.cousyncdb.CouSyncDb;
import cn.dengx.cousyncdb.OnOperateFinish;
import cn.dengx.sample.Bean.Bean1;

/**
 * Current project:CouSyncDb.
 * Created by dengx on 16/1/20,13:43.
 */
public class MainActivity extends Activity {

    private TextView text;
    private Bean1 bean1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        text = (TextView) findViewById(R.id.text);
        initModel();

        findViewById(R.id.insert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initModel();
//                long l = CouSyncDb.getInstance(getApplicationContext()).insert(bean1);
//                text.setText("insert row ID = "+String.valueOf(l));

                CouSyncDb.getInstance(getApplicationContext()).insertSync(bean1, new OnOperateFinish() {
                    @Override
                    public void onFinish(int operateType, Object result) {
                        text.setText("insert row ID = " + result.toString());
                    }
                });
            }
        });

        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int ID =CouSyncDb.getInstance(getApplicationContext()).delete(bean1);
//                text.setText("delete row ID = "+String.valueOf(ID));

                CouSyncDb.getInstance(getApplicationContext()).deleteSync(bean1, new OnOperateFinish() {
                    @Override
                    public void onFinish(int operateType, Object result) {
                        text.setText("delete row ID = " + result.toString());
                    }
                });
            }
        });

        findViewById(R.id.replace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bean1.setaBoolean(false);
                bean1.setaByte((byte) 118);
//                long l =CouSyncDb.getInstance(getApplicationContext()).replace(bean1);
//                text.setText("replace row "+ String.valueOf(l));

                CouSyncDb.getInstance(getApplicationContext()).replaceSync(bean1, new OnOperateFinish() {
                    @Override
                    public void onFinish(int operateType, Object result) {
                        text.setText("replace row " + result.toString());
                    }
                });
            }
        });

        findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bean1.setaDouble(3.1415926);
                short a = 15;
                bean1.setaShort(a);
//                int i =CouSyncDb.getInstance(getApplicationContext()).update(bean1);
//                text.setText("update rows "+String.valueOf(i));

                CouSyncDb.getInstance(getApplicationContext()).updateSync(bean1, new OnOperateFinish() {
                    @Override
                    public void onFinish(int operateType, Object result) {
                        text.setText("update rows " + result.toString());
                    }
                });
            }
        });

        findViewById(R.id.query).
                setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
//                Object o =CouSyncDb.getInstance(getApplicationContext()).rawQuery(
//                        "select * from bean1 where anInt=88",null,Bean1.class);
//                if(o!=null) {
//                    List<Bean1> datas = (List<Bean1>) o;
//                    if(datas.size()>0)
//                        text.setText(datas.get(0).toString());
//                }

//                Object o = CouSyncDb.getInstance(getApplicationContext()).rawQuery(
//                        "select * from bean1 where anInt=88", null, null);
//                if (o != null) {
//                    Cursor cursor = (Cursor) o;
//                    text.setText(DatabaseUtils.dumpCursorToString(cursor));
//                    cursor.close();
//                }

                                           CouSyncDb.getInstance(getApplicationContext()).rawQuerySync("select * from bean1 where anInt=88", null, Bean1.class,
                                                   new OnOperateFinish() {
                                                       @Override
                                                       public void onFinish(int operateType, Object result) {
                                                           if (result != null) {
                                                               List<Bean1> datas = (List<Bean1>) result;
                                                               if (datas.size() > 0)
                                                                   text.setText(datas.get(0).toString());
                                                           }
                                                       }
                                                   }

                                           );
                                       }
                                   }

                );
    }

    private void initModel() {
        bean1 = new Bean1();
        bean1.setaBoolean(true);
        bean1.setaFloat(11.44f);
        bean1.setaString("welcome 哈哈");
        bean1.setAnInt(88);
    }
}
