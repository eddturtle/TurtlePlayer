/*
 * 
 * TURTLE PLAYER
 * 
 * Licensed under MIT & GPL
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * Created by Edd Turtle (www.eddturtle.co.uk)
 * More Information @ www.turtle-player.co.uk
 * 
 */

// Package
package turtle.player.dirchooser;

// Import - Java

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import turtle.player.R;

import java.io.File;

/**
 * Activity with result to choose a folder.
 * Input: Extra / String / {@link DirChooserConstants#ACTIVITY_PARAM_KEY_DIR_CHOOSER_INITIAL_DIR}
 * (absolute path)
 * Output: Extra / String / {@link DirChooserConstants#ACTIVITY_RETURN_KEY_DIR_CHOOSER_CHOOSED_DIR}
 * (absolute path with trailing {@link DirChooserConstants#PATH_SEPERATOR})
 */
public class DirChooser extends Activity
{

    private ListView dirList;
    private TextView currDirLabel;

    private ImageView upButton;
    private ImageView chooseDir;

    private File currDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dir_chooser);

        init();
    }

    private void init()
    {
        dirList =  (ListView) findViewById(R.id.dirList);
        currDirLabel = (TextView) findViewById(R.id.currDirLabel);
        upButton  = (ImageView) findViewById(R.id.up);
        chooseDir = (ImageView) findViewById(R.id.choose_dir);

        final String initialDir = getIntent().getExtras().getString(
                DirChooserConstants.ACTIVITY_PARAM_KEY_DIR_CHOOSER_INITIAL_DIR);
        setCurrDir(getExistingParentFolderFile(initialDir));

        initListeners();
    }

    private void initListeners() {
        dirList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                File selectedFile = currDir.listFiles()[position];
                if (selectedFile.isDirectory()) {
                    setCurrDir(selectedFile);
                }
            }
        });

        chooseDir.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = getIntent();
                intent.putExtra(DirChooserConstants.ACTIVITY_RETURN_KEY_DIR_CHOOSER_CHOOSED_DIR,
                        currDir.getAbsolutePath() + DirChooserConstants.PATH_SEPERATOR);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        upButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                setCurrDir(currDir.getParentFile());
            }
        });
    }

    private void setCurrDir(File file){
        currDir = file;
        currDirLabel.setText(file.getAbsolutePath());
        setAdapterForDir(file);
        toggleUpButton(file);
    }

    private void toggleUpButton(File file){
        upButton.setEnabled(file.getParentFile() != null);
    }

    private void setAdapterForDir(File file){
        ArrayAdapter<File> adapter = new FileAdapter(this, file.listFiles());
        dirList.setAdapter(adapter);
    }

    private File getExistingParentFolderFile(String path){
        if(path == null || !path.startsWith(DirChooserConstants.PATH_SEPERATOR)){
            return new File(DirChooserConstants.PATH_SEPERATOR);
        }

        //Go up untill the string represents a valid directory
        while(!new File(path).isDirectory()){
            String pathWihoutTrailngSep = path.endsWith(DirChooserConstants.PATH_SEPERATOR) ?
                    path.substring(0, path.length() - DirChooserConstants.PATH_SEPERATOR.length()) :
                    path;

            path = pathWihoutTrailngSep.substring(0, path.lastIndexOf(DirChooserConstants.PATH_SEPERATOR));
        }
        return new File(path);
    }
}