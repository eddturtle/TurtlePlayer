/**
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
 * More Information @ www.turtle-player.co.uk
 *
 * @author Simon Honegger (Hoene84)
 */

package com.turtleplayer.dirchooser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.turtleplayer.R;

import java.io.File;
import java.util.Stack;

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

	private Stack<File> navigationStack = new Stack<File>();
	private File initialDir;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dir_chooser);

		init();
	}

	private void init()
	{
		dirList = (ListView) findViewById(R.id.dirList);
		currDirLabel = (TextView) findViewById(R.id.currDirLabel);
		upButton = (ImageView) findViewById(R.id.up);
		chooseDir = (ImageView) findViewById(R.id.choose_dir);

		final String initialDirPath = getIntent().getExtras().getString(
				  DirChooserConstants.ACTIVITY_PARAM_KEY_DIR_CHOOSER_INITIAL_DIR);
		initialDir = new File(initialDirPath);
		setCurrDir(initialDir);

		initListeners();
	}

	private void initListeners()
	{
		dirList.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent,
											View view,
											int position,
											long id)
			{
				File selectedFile = (File) parent.getItemAtPosition(position);
				if (selectedFile.isDirectory())
				{
					if(initialDir != null &&
							  selectedFile.getAbsolutePath().contains(initialDir.getAbsolutePath()) &&
							  !initialDir.equals(selectedFile))
					{
						navigationStack.push(currDir);
					}
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
				navigationStack.remove(currDir.getParentFile());
				setCurrDir(currDir.getParentFile());
			}
		});
	}

	private void setCurrDir(File file)
	{
		currDir = file;
		currDirLabel.setText(file.getAbsolutePath());
		setAdapterForDir(file);
		toggleUpButton(file);
	}

	private void toggleUpButton(File file)
	{
		upButton.setEnabled(file.getParentFile() != null);
	}

	private void setAdapterForDir(File file)
	{
		File[] files = file.listFiles();
		if (files == null)
		{
			files = new File[0];
		}
		ArrayAdapter<File> adapter = new FileAdapter(this, files);
		dirList.setAdapter(adapter);
	}

	@Override
	public void onBackPressed()
	{
		if(navigationStack.empty())
		{
			super.onBackPressed();
		}
		else
		{
			setCurrDir(navigationStack.pop());
		}
	}
}