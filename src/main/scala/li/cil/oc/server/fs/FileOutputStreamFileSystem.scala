package li.cil.oc.server.fs

import java.io
import java.io.RandomAccessFile
import li.cil.oc.api.fs.Mode

trait FileOutputStreamFileSystem extends FileInputStreamFileSystem with OutputStreamFileSystem {
  override def spaceTotal = -1

  override def spaceUsed = -1

  // ----------------------------------------------------------------------- //

  override def delete(path: String) = new io.File(root, path).delete()

  override def makeDirectory(path: String) = new io.File(root, path).mkdir()

  override def rename(from: String, to: String) = new io.File(root, from).renameTo(new io.File(root, to))

  override def setLastModified(path: String, time: Long) = new io.File(root, path).setLastModified(time)

  // ----------------------------------------------------------------------- //

  override protected def openOutputHandle(id: Int, path: String, mode: Mode): Option[OutputHandle] =
    Some(new FileHandle(new RandomAccessFile(new io.File(root, path), mode match {
      case Mode.Append | Mode.Write => "rw"
      case _ => throw new IllegalArgumentException()
    }), this, id, path))

  protected class FileHandle(val file: RandomAccessFile, owner: OutputStreamFileSystem, handle: Int, path: String) extends OutputHandle(owner, handle, path) {
    override def position() = file.getFilePointer

    override def length() = file.length()

    override def close() {
      super.close()
      file.close()
    }

    override def seek(to: Long) = {
      file.seek(to)
      to
    }

    override def write(value: Array[Byte]) = file.write(value)
  }

}