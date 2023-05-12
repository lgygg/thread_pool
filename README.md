# init

```
DownloadManager.getInstance().setContext(getApplicationContext());
```

# observer

```
private DataWatcher dataWatcher = new DataWatcher() {

   @Override
   public void onDataChanged(TaskBean downloadEntry) {
      handler.post(new Runnable() {
         @Override
         public void run() {
            downloadEntries.put(downloadEntry.url,downloadEntry);
            adapter.notifyDataSetChanged();
         }
      });

   }
};
```

```
DownloadManager.getInstance().addObserver(dataWatcher);
```

# use

download

```
DownloadManager.getInstance().execute(task);
```

pause

```
DownloadManager.getInstance().pause(entry.id);
```