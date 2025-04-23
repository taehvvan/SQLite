package com.example.sqlite;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener {

    private DbAdapter dbAdapter;
    private SimpleCursorAdapter mAdapter;
    private EditText eText1, eText2, eText3, eText4;
    private Button insert_btn, update_btn, delete_btn;
    ListView list01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eText1 = (EditText)findViewById(R.id.editText01);
        eText2 = (EditText)findViewById(R.id.editText02);
        eText3 = (EditText)findViewById(R.id.editText03);
        eText4 = (EditText)findViewById(R.id.editText04);
        eText1.setEnabled(false);

        insert_btn = (Button)findViewById(R.id.button01);
        update_btn = (Button)findViewById(R.id.button02);
        delete_btn = (Button)findViewById(R.id.button03);
        insert_btn.setOnClickListener(this);
        update_btn.setOnClickListener(this);
        delete_btn.setOnClickListener(this);

        list01 = (ListView)findViewById(R.id.list01);
        list01.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        dbAdapter = new DbAdapter(this);
        dbAdapter.open();
        Cursor c = dbAdapter.fetchAllNotes();

        String[] from = new String[] {BaseColumns._ID,
                DbAdapter.KEY_TITLE,
                DbAdapter.KEY_BODY,
                DbAdapter.KEY_ETC
        };

        int[] to = new int[] { R.id._id, R.id.title, R.id.body, R.id.etc };

        mAdapter = new SimpleCursorAdapter(
                this, R.layout.data_row, c, from, to, 0);

        list01.setAdapter(mAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dbAdapter.close();
    }

    @Override
    public void onClick(View view) {
        String txt1, txt2, txt3, txt4;

        if(view.getId() == R.id.button01) {
            txt2 = eText2.getText().toString().trim();
            txt3 = eText3.getText().toString().trim();
            txt4 = eText4.getText().toString().trim();
            if(!txt2.isEmpty() && !txt3.isEmpty() && !txt4.isEmpty()){
                dbAdapter.insertNote(txt2, txt3, txt4);
                toastMemo("insert - success");
            }else{
                toastMemo("insert - failure");
            }
        }

        if(view.getId() == R.id.button02) {
            txt1 = eText1.getText().toString().trim();
            txt2 = eText2.getText().toString().trim();
            txt3 = eText3.getText().toString().trim();
            txt4 = eText4.getText().toString().trim();
            if(!txt2.isEmpty() && !txt3.isEmpty()){
                if(dbAdapter.updateNote(txt1, txt2, txt3, txt4))
                    toastMemo("update - success");
                else
                    toastMemo("update - failure");
            }
        }
        if(view.getId() == R.id.button03) {
            txt1 = eText1.getText().toString().trim();
            if(dbAdapter.deleteNote(txt1))
                toastMemo("delete - success");
            else
                toastMemo("delete - failure");
        }

        eText1.setText("");
        eText2.setText("");
        eText3.setText("");
        eText4.setText("");
        setEnabled(false);
        Cursor c = dbAdapter.fetchAllNotes();
        mAdapter.changeCursor(c);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ConstraintLayout container = (ConstraintLayout) view;
        TextView id_text = (TextView)container.findViewById(R.id._id);
        TextView title_text = (TextView)container.findViewById(R.id.title);
        TextView body_text = (TextView) container.findViewById(R.id.body);
        TextView etc_text = (TextView) container.findViewById(R.id.etc);
        eText1.setText(id_text.getText());
        eText2.setText(title_text.getText());
        eText3.setText(body_text.getText());
        eText4.setText(etc_text.getText());
        setEnabled(true);
    }

    private void setEnabled(boolean enabled) {
        update_btn.setEnabled(enabled);
        delete_btn.setEnabled(enabled);
    }

    private void toastMemo(String str) {
        if(str.isEmpty())
            return;
        Toast toast = Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG);
        toast.show();
    }

}